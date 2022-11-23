package com.rafaelcosio.mathhelper.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.databinding.ActivitySolveBinding;
import com.rafaelcosio.mathhelper.models.Answer;
import com.rafaelcosio.mathhelper.models.Problem;
import com.rafaelcosio.mathhelper.utils.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rafaelcosio.mathhelper.utils.Constants.CHEATS_USED;
import static com.rafaelcosio.mathhelper.utils.Constants.ELAPSED_TIME;
import static com.rafaelcosio.mathhelper.utils.Constants.EMPTY_STRING;
import static com.rafaelcosio.mathhelper.utils.Constants.LEVEL;
import static com.rafaelcosio.mathhelper.utils.Constants.OPERATION;
import static com.rafaelcosio.mathhelper.utils.Constants.PROBLEMS_DATA;
import static com.rafaelcosio.mathhelper.utils.Constants.SCORE;
import static com.rafaelcosio.mathhelper.utils.Constants.SOLVED_PROBLEMS;
import static com.rafaelcosio.mathhelper.utils.Constants.TOTAL_PROBLEMS;

public class SolveActivity extends AppCompatActivity {
    private final String TAG = SolveActivity.class.getSimpleName();
    private final int totalProblems = 8;
    private final boolean[] answered = new boolean[totalProblems];
    private List<Problem> problemList;
    private final boolean[] correct = new boolean[totalProblems];
    private int actualProblemIndex = 0;
    private int selectedAnswer = 0;
    private int score = 0;
    private int level = 0;
    private int operationsType = 0;
    private ActivitySolveBinding binding;
    private Problem actualProblem;
    private boolean chronometerPaused = false;
    private int tryAgainQty = 2;
    private int pauseTimeQty = 3;
    private int revealAnswerQty = 1;
    private int cheatsUsed = 0;
    private int problemsSolved = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_solve);
        Intent i = getIntent();
        level = i.getIntExtra(LEVEL, 0);
        operationsType = i.getIntExtra(OPERATION, 0);
        Log.d(TAG, "Level: " + level);
        Log.d(TAG, "Operations: " + operationsType);
        binding.chronometer.setBase(SystemClock.elapsedRealtime());
        binding.chronometer.start();
        problemList = fillProblemsList();
        updateAll();
        binding.fab.setOnClickListener(v -> checkAnswer());
        binding.buttonTryAgain.setOnClickListener(v -> {
            cheatsUsed++;
            tryAgainQty--;
            actualProblem.setSolved(false);
            updateAll();
        });
        binding.buttonPauseTime.setOnClickListener(v -> {
            if (!chronometerPaused) {
                pauseTimeQty--;
                cheatsUsed++;
                chronometerPaused = true;
                long timeWhenStopped = binding.chronometer.getBase() - SystemClock.elapsedRealtime();
                binding.chronometer.setTextColor(Color.BLUE);
                binding.chronometer.stop();
                binding.chronometer.setText("PAUSADO");
                updateCheatButtons();
                new Handler().postDelayed(() -> {
                    binding.chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    binding.chronometer.start();
                    binding.chronometer.setTextColor(Color.BLACK);
                    chronometerPaused = false;
                }, 5000);
            } else {
                Toast.makeText(this, "El cronómetro ya está pausado", Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonRevealAnswer.setOnClickListener(v -> {
            revealAnswerQty--;
            cheatsUsed++;
            actualProblem.setSolved(true);
            actualProblem.setCorrect(true);
            actualProblem.setGivenAnswer(actualProblem.getResult());
            updateAll();
        });
        binding.indicator.setStepCount(totalProblems);
        binding.indicator.showStepNumberInstead(true);
        binding.indicator.useBottomIndicator(true);
        binding.indicator.addOnStepClickListener(step -> {
            actualProblemIndex = step;
            updateAll();
        });

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_one:
                    selectedAnswer = 0;
                    break;
                case R.id.radio_two:
                    selectedAnswer = 1;
                    break;
                case R.id.radio_three:
                    selectedAnswer = 2;
                    break;
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            actualProblemIndex--;
            Animation a = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
            a.reset();
            binding.cardView.clearAnimation();
            binding.cardView.startAnimation(a);
            updateAll();
        });

        binding.btnForward.setOnClickListener(v -> {
            if (actualProblem.isMultipleAnswer()) {
                if (binding.radioOne.isChecked() || binding.radioTwo.isChecked() || binding.radioThree.isChecked()) {
                    checkAnswer();
                    if (actualProblem.isCorrect()) {
                        Snackbar.make(binding.container, "El problema anterior se respondió correctamente", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(binding.container, "El problema anterior se respondió erróneamente", Snackbar.LENGTH_LONG).show();
                    }
                }
            } else {
                if (!binding.answer.getText().toString().isEmpty() && !actualProblem.isSolved()) {
                    checkAnswer();
                    if (actualProblem.isCorrect()) {
                        Snackbar.make(binding.container, "El problema anterior se respondió correctamente", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(binding.container, "El problema anterior se respondió erróneamente", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            if (actualProblemIndex >= totalProblems) {
                actualProblemIndex = totalProblems - 1;
            } else {
                actualProblemIndex++;
            }
            binding.btnBack.setEnabled(true);
            if (actualProblemIndex >= totalProblems) {
                boolean finished = true;
                updateBooleans();
                for (boolean s : answered) {
                    if (!s) {
                        finished = false;
                        new MaterialDialog.Builder(this)
                                .title("Quedan preguntas sin resolver")
                                .content("¿Está seguro que desea salir?")
                                .positiveText(android.R.string.yes)
                                .negativeText(android.R.string.no)
                                .onPositive((dialog1, which) -> {

                                    Intent intent = new Intent(this, SummaryActivity.class);
                                    intent.putExtra(PROBLEMS_DATA, Parcels.wrap(problemList));
                                    intent.putExtra(ELAPSED_TIME, SystemClock.elapsedRealtime() - binding.chronometer.getBase());
                                    intent.putExtra(SCORE, score);
                                    intent.putExtra(CHEATS_USED, cheatsUsed);
                                    intent.putExtra(SOLVED_PROBLEMS, problemsSolved);
                                    intent.putExtra(TOTAL_PROBLEMS, totalProblems);
                                    intent.putExtra(LEVEL, level);
                                    intent.putExtra(OPERATION, operationsType);
                                    startActivity(intent);
                                    this.setResult(Activity.RESULT_OK);
                                    finish();

                                })
                                .onNegative((dialog1, which) -> {
                                    actualProblemIndex--;
                                    updateLayout();
                                })
                                .show();
                        break;
                    }
                }
                if (finished) {
                    Intent intent = new Intent(this, SummaryActivity.class);
                    intent.putExtra(PROBLEMS_DATA, Parcels.wrap(problemList));
                    intent.putExtra(ELAPSED_TIME, SystemClock.elapsedRealtime() - binding.chronometer.getBase());
                    intent.putExtra(SCORE, score);
                    intent.putExtra(CHEATS_USED, cheatsUsed);
                    intent.putExtra(SOLVED_PROBLEMS, problemsSolved);
                    intent.putExtra(TOTAL_PROBLEMS, totalProblems);
                    intent.putExtra(LEVEL, level);
                    intent.putExtra(OPERATION, operationsType);
                    startActivity(intent);
                    this.setResult(Activity.RESULT_OK);
                    finish();
                }
            } else {
                Animation a = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
                a.reset();
                binding.cardView.clearAnimation();
                binding.cardView.startAnimation(a);
                updateAll();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("¿Salir?")
                .content("¿Está seguro que desea salir?")
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive((dialog1, which) -> {
                    this.setResult(Activity.RESULT_CANCELED);
                    super.onBackPressed();
                })
                .show();

    }

    private List<Problem> fillProblemsList() {
        List<Problem> problemList = new ArrayList<>();
        Utils.randomNumber(5);
        int seed;
        int lastSeed = 0;
        for (int x = 0; x < totalProblems; x++) {
            seed = Utils.randomNumber(5);
            while (seed == lastSeed) {
                seed = Utils.randomNumber(5);
            }
            Problem problem = new Problem().generateProblem(this, operationsType, level, seed);
            problemList.add(problem);
            lastSeed = seed;
        }
        return problemList;
    }

    private void updateAll() {
        updateProblem();
        updateLayout();
        updateBooleans();
        updateCheatButtons();
    }

    private void updateCheatButtons() {
        binding.buttonRevealAnswer.setLabelText(getResources().getString(R.string.reveal_answer, revealAnswerQty));
        binding.buttonPauseTime.setLabelText(getResources().getString(R.string.pause_time, pauseTimeQty));
        binding.buttonTryAgain.setLabelText(getResources().getString(R.string.try_again, tryAgainQty));
        if (actualProblem.isSolved()) {
            if (actualProblem.isCorrect()) {
                enableButton(binding.buttonTryAgain, false);
                enableButton(binding.buttonRevealAnswer, false);
                enableButton(binding.buttonPauseTime, false);
            } else {
                enableButton(binding.buttonTryAgain, hasCheatsLeft(tryAgainQty));
                enableButton(binding.buttonRevealAnswer, false);
                enableButton(binding.buttonPauseTime, false);
            }
        } else {
            enableButton(binding.buttonPauseTime, hasCheatsLeft(pauseTimeQty));
            enableButton(binding.buttonRevealAnswer, hasCheatsLeft(revealAnswerQty));
            enableButton(binding.buttonTryAgain, false);
        }
    }

    private void enableButton(FloatingActionButton button, boolean enabled) {
        button.setEnabled(enabled);
    }

    private void updateBooleans() {
        for (int x = 0; x < problemList.size(); x++) {
            answered[x] = problemList.get(x).isSolved();
            correct[x] = problemList.get(x).isCorrect();
        }
        binding.indicator.setAnswered(answered);
        binding.indicator.setCorrect(correct);
    }

    private void updateProblem() {
        actualProblem = problemList.get(actualProblemIndex);
    }

    private void updateLayout() {
        hideKeyboard();
        if (binding.fabMenu.isOpened()) {
            binding.fabMenu.close(true);
        }
        setTitle(getResources().getString(R.string.problem_index, actualProblemIndex + 1, totalProblems));
        if (actualProblemIndex == 0) {
            binding.btnBack.setEnabled(false);
        } else {
            binding.btnBack.setEnabled(true);
        }
        if (actualProblemIndex >= totalProblems - 1) {
            binding.btnForward.setText(getResources().getString(R.string.finish));
        } else {
            binding.btnForward.setText(getResources().getString(R.string.bottom_forward));
        }
        binding.message.setText(actualProblem.getDescription());
        binding.exampleImage.setImageResource(actualProblem.getImageID());
        binding.indicator.setCurrentStep(actualProblemIndex);
        binding.score.setText(getResources().getString(R.string.score, score));
        binding.buttonRevealAnswer.setLabelText(getResources().getString(R.string.reveal_answer, revealAnswerQty));
        binding.buttonPauseTime.setLabelText(getResources().getString(R.string.pause_time, pauseTimeQty));
        binding.buttonTryAgain.setLabelText(getResources().getString(R.string.try_again, tryAgainQty));

        //region Multiple Answers Problems

        if (actualProblem.isMultipleAnswer()) {
            binding.answer.setVisibility(View.GONE);
            binding.radioGroup.setVisibility(View.VISIBLE);
            binding.radioGroup.clearCheck();
            List<Answer> possibleAnswers = actualProblem.getPossibleAnswers();
            Answer correctAnswer = possibleAnswers.get(0);
            Collections.shuffle(possibleAnswers);
            binding.radioOne.setText(possibleAnswers.get(0).getDesc());
            binding.radioTwo.setText(possibleAnswers.get(1).getDesc());
            binding.radioThree.setText(possibleAnswers.get(2).getDesc());
            if (actualProblem.isSolved()) {
                binding.radioOne.setEnabled(false);
                binding.radioTwo.setEnabled(false);
                binding.radioThree.setEnabled(false);
                if (possibleAnswers.get(0).getResult() == actualProblem.getGivenAnswer()) {
                    binding.radioOne.setChecked(true);
                } else if (possibleAnswers.get(1).getResult() == actualProblem.getGivenAnswer()) {
                    binding.radioTwo.setChecked(true);
                } else if (possibleAnswers.get(2).getResult() == actualProblem.getGivenAnswer()) {
                    binding.radioThree.setChecked(true);
                }
                if (actualProblem.isCorrect()) {
                    binding.result.setText(getResources().getString(R.string.correct));
                    binding.result.setTextColor(Color.BLUE);
                } else {
                    binding.result.setText(getResources().getString(R.string.wrong_ans, correctAnswer.getDesc()));
                    binding.result.setTextColor(Color.RED);
                }
            } else {
                binding.radioOne.setEnabled(true);
                binding.radioTwo.setEnabled(true);
                binding.radioThree.setEnabled(true);
                binding.result.setTextColor(Color.BLACK);
                binding.result.setText(getResources().getString(R.string.not_answered));
            }
        }
        //endregion

        //region Written Problems

        else {
            binding.radioGroup.setVisibility(View.GONE);
            binding.answer.setVisibility(View.VISIBLE);
            if (actualProblem.isSolved()) {
                binding.answer.setEnabled(false);
                binding.answer.setText(getResources().getString(R.string.result, actualProblem.getGivenAnswer()));
                if (actualProblem.isCorrect()) {
                    binding.result.setText(getResources().getString(R.string.correct));
                    binding.result.setTextColor(Color.BLUE);
                    binding.answer.setError(null);
                } else {
                    binding.result.setText(getResources().getString(R.string.wrong, actualProblem.getResult()));
                    binding.result.setTextColor(Color.RED);
                    binding.answer.setError(getResources().getString(R.string.wrong, actualProblem.getResult()));
                }
            } else {
                binding.answer.setEnabled(true);
                binding.answer.setError(null);
                binding.answer.setText(EMPTY_STRING);
                binding.result.setTextColor(Color.BLACK);
                binding.result.setText(getResources().getString(R.string.not_answered));
            }
        }

        //endregion
    }

    private boolean hasCheatsLeft(int cheat) {
        return cheat > 0;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void checkAnswer() {
        if (!actualProblem.isSolved()) {
            if (actualProblem.isMultipleAnswer()) {
                if (binding.radioOne.isChecked() || binding.radioTwo.isChecked() || binding.radioThree.isChecked()) {
                    actualProblem.answer(actualProblem.getPossibleAnswers().get(selectedAnswer).getResult());
                } else {
                    Toast.makeText(this, "Selecciona una respuesta", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!binding.answer.getText().toString().isEmpty()) {
                    actualProblem.answer(Integer.parseInt(binding.answer.getText().toString()));
                } else {
                    Toast.makeText(this, "Escribe una respuesta", Toast.LENGTH_SHORT).show();
                }
            }
            if (actualProblem.isCorrect()) {
                score += 100;
                problemsSolved++;
            }
            updateCheatButtons();
            updateLayout();
        }
    }

}
