package com.rafaelcosio.mathhelper.views.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.databinding.ActivitySummaryBinding;
import com.rafaelcosio.mathhelper.models.Problem;
import com.rafaelcosio.mathhelper.views.adapters.ProblemAdapter;

import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.rafaelcosio.mathhelper.utils.Constants.CHEATS_USED;
import static com.rafaelcosio.mathhelper.utils.Constants.ELAPSED_TIME;
import static com.rafaelcosio.mathhelper.utils.Constants.LEVEL;
import static com.rafaelcosio.mathhelper.utils.Constants.OPERATION;
import static com.rafaelcosio.mathhelper.utils.Constants.PROBLEMS_DATA;
import static com.rafaelcosio.mathhelper.utils.Constants.SCORE;
import static com.rafaelcosio.mathhelper.utils.Constants.SOLVED_PROBLEMS;
import static com.rafaelcosio.mathhelper.utils.Constants.TOTAL_PROBLEMS;

public class SummaryActivity  extends AppCompatActivity {

    private List<Problem> problemList;
    private int score;
    private int cheatsUsed;
    private int solvedProblems;
    private int totalProblems;
    private int level;
    private Long elapsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySummaryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_summary);

        Intent i = getIntent();
        elapsedTime = i.getLongExtra(ELAPSED_TIME, 0);
        score = i.getIntExtra(SCORE, 0);
        cheatsUsed = i.getIntExtra(CHEATS_USED, 0);
        solvedProblems = i.getIntExtra(SOLVED_PROBLEMS, 0);
        totalProblems = i.getIntExtra(TOTAL_PROBLEMS, 0);
        level = i.getIntExtra(LEVEL,0);
        int operationType = i.getIntExtra(OPERATION, 0);

        String[] operations = getResources().getStringArray(R.array.operaciones);
        String[] levels = getResources().getStringArray(R.array.dificultad);

        List<Problem> problems = Parcels.unwrap(i.getParcelableExtra(PROBLEMS_DATA));
        if (problems != null) {
            problemList = problems;
        }

        calculateScore();

        MainActivity.addPointsToUser(score);

        binding.operaciones.setText(getResources().getString(R.string.operacion,operations[operationType]));
        binding.dificultad.setText(getResources().getString(R.string.dificultad,levels[level]));
        binding.tiempoTranscurrido.setText(getResources().getString(R.string.tiempo, convertTime(elapsedTime)));
        binding.trucosUsados.setText(getResources().getString(R.string.trucos_usados, cheatsUsed));
        binding.problemasResueltos.setText(getResources().getString(R.string.problemas_resueltos, solvedProblems, totalProblems));
        binding.puntuacion.setText(getResources().getString(R.string.puntuacion, score));

        ProblemAdapter adapter = new ProblemAdapter(problemList);
        binding.problemsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.problemsRecycler.setItemAnimator(new DefaultItemAnimator());
        binding.problemsRecycler.setAdapter(adapter);
        binding.buttonFinalizar.setOnClickListener(v -> finish());
    }

    private void calculateScore() {
        score *= (level+1);
        if(elapsedTime<= TimeUnit.MINUTES.toMillis(1)){
            score*=3;
        }else if(elapsedTime<=TimeUnit.MINUTES.toMillis(2)){
            score*=1.5;
        }
        if(cheatsUsed==0&&solvedProblems>1){
            score+=1000;
        }
        if(solvedProblems==totalProblems){
            score*=5;
        }
    }

    private String convertTime(Long millis) {
        return String.format(Locale.getDefault(), "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}