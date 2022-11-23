package com.rafaelcosio.mathhelper.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.models.Answer;
import com.rafaelcosio.mathhelper.models.Problem;

import java.util.List;

public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ProblemVH> {
    //region Variables & Contructors
    private final List<Problem> problems;

    public ProblemAdapter(List<Problem> problems) {
        this.problems = problems;
    }
    //endregion

    //region Adapter methods
    @NonNull
    @Override
    public ProblemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_problem, parent, false);
        ProblemVH holder = new ProblemVH(mView);
        mView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemVH holder, int position) {
        Problem problem = problems.get(position);
        Context context = holder.itemView.getContext();
        holder.statement.setText(problem.getDescription());

        if (problem.isMultipleAnswer()) {
            if (problem.isSolved()) {
                List<Answer> possibleAnswers = problem.getPossibleAnswers();
                Answer givenAnswer = new Answer();
                for (Answer answer : possibleAnswers) {
                    if (answer.getResult() == problem.getGivenAnswer()) {
                        givenAnswer = answer;
                        break;
                    }
                }
                holder.answer.setText(context.getResources().getString(R.string.your_answer_string, givenAnswer.getDesc()));
                if (!problem.isCorrect()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_wrong_black_24dp));
                    }
                    holder.answer.setTextColor(Color.RED);
                    holder.realAnswer.setText(context.getResources().getString(R.string.wrong_ans, possibleAnswers.get(0).getDesc()));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    holder.answer.setTextColor(Color.BLACK);
                    holder.realAnswer.setText(context.getResources().getString(R.string.correct));
                }
            } else {
                holder.realAnswer.setText("Sin contestar");
                holder.answer.setText("Sin contestar");
            }
        } else {
            if (problem.isSolved()) {
                holder.answer.setText(context.getResources().getString(R.string.your_answer, problem.getGivenAnswer()));
                if (!problem.isCorrect()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_wrong_black_24dp));
                    }
                    holder.answer.setTextColor(Color.RED);
                    holder.realAnswer.setText(context.getResources().getString(R.string.wrong_ans, String.valueOf(problem.getResult())));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ic_check_black_24dp));
                    }
                    holder.answer.setTextColor(Color.BLACK);
                    holder.realAnswer.setText(context.getResources().getString(R.string.correct));
                }
            } else {
                holder.realAnswer.setText("Sin contestar");
                holder.answer.setText("Sin contestar");
            }
        }
    }


    @Override
    public int getItemCount() {
        return problems.size();
    }
    //endregion

    //endregion

    //region ViewHolder
    public static class ProblemVH extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView statement;
        final TextView realAnswer;
        final TextView answer;

        ProblemVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            statement = itemView.findViewById(R.id.problem_statement);
            answer = itemView.findViewById(R.id.problem_given_answer);
            realAnswer = itemView.findViewById(R.id.problem_real_answer);
        }
    }
    //endregion
}
