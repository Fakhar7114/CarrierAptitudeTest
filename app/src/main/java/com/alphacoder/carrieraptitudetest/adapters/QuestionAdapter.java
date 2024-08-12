package com.alphacoder.carrieraptitudetest.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacoder.carrieraptitudetest.R;
import com.alphacoder.carrieraptitudetest.databinding.ItemAnswersBinding;
import com.alphacoder.carrieraptitudetest.databinding.ItemQuestionBinding;
import com.alphacoder.carrieraptitudetest.databinding.ItemQuestionCategoryBinding;
import com.alphacoder.carrieraptitudetest.models.Questions;

import java.util.HashMap;
import java.util.Map;

public class QuestionAdapter extends ListAdapter<Questions, RecyclerView.ViewHolder> {

    String TAG = "QuestionsAdapter";

    Context context;
    boolean isCategory = false;
    boolean isAnswer = false;
    QuestionCallback callback;
    Map<Integer, Questions> questionsMap;
    Map<String, Integer> correctAnswers;
    Map<Integer,Boolean> previousAnswerStatus;


    public QuestionAdapter(Context context, boolean isCategory, boolean isAnswer, @NonNull DiffUtil.ItemCallback<Questions> diffCallback, QuestionCallback callback) {
        super(diffCallback);
        this.context = context;
        this.isCategory = isCategory;
        this.callback = callback;
        questionsMap = new HashMap<>();
        correctAnswers = new HashMap<>();
        previousAnswerStatus=new HashMap<>();
        this.isAnswer = isAnswer;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemQuestionBinding binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ItemQuestionCategoryBinding binding1 = ItemQuestionCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ItemAnswersBinding binding2 = ItemAnswersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        if (isCategory) {
            return new QuestionCategoryHolder(binding1);
        } else if (isAnswer) {
            return new AnswerHolder(binding2);
        } else {
            return new QuestionHolder(binding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Questions question = getItem(position);

        // Initialize or update correctAnswers map for each question category
        if (!correctAnswers.containsKey(question.getCategory())) {
            correctAnswers.put(question.getCategory(), 0);
        }
        // Initialize or update previous Answer State map for each question category
        if (!previousAnswerStatus.containsKey(question.getId())) {
            previousAnswerStatus.put(question.getId(), false);
        }

        if (holder instanceof QuestionHolder) {
            ((QuestionHolder) holder).bind(context, question, callback, questionsMap, correctAnswers,previousAnswerStatus);
        } else if (holder instanceof QuestionCategoryHolder) {
            ((QuestionCategoryHolder) holder).bind(context, question, callback, questionsMap, correctAnswers,previousAnswerStatus);

        } else if (holder instanceof AnswerHolder) {
            ((AnswerHolder) holder).bind(context, question);
        }

    }

    public static class QuestionHolder extends RecyclerView.ViewHolder {

        String TAG = "QuestionHolder";

        ItemQuestionBinding binding;

        public QuestionHolder(@NonNull ItemQuestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Context context, Questions question, QuestionCallback callback, Map<Integer, Questions> questionsMap, Map<String, Integer> correctAnswers, Map<Integer, Boolean> previousAnswerStatus) {


            String answer = question.getAnswer();
            binding.tvCategory.setText(question.getCategory());
            binding.tvQuestion.setText(question.getQuestion());

            binding.optionA.setText(question.getOptions().get(0));
            binding.optionB.setText(question.getOptions().get(1));
            binding.optionC.setText(question.getOptions().get(2));
            binding.optionD.setText(question.getOptions().get(3));

            binding.radioGroup.setOnCheckedChangeListener(null);
            binding.radioGroup.clearCheck();

            int selectedPos = question.getSelectedOption();
            if (selectedPos != -1) {
                switch (selectedPos) {

                    case 0:
                        binding.optionA.setChecked(true);
                        break;
                    case 1:
                        binding.optionB.setChecked(true);
                        break;
                    case 2:
                        binding.optionC.setChecked(true);
                        break;
                    case 3:
                        binding.optionD.setChecked(true);
                        break;
                    default:
                        binding.radioGroup.clearCheck();
                }
            }

            binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {


                    int newSelectedOption = -1;

                    if (checkedId == binding.optionA.getId()) {
                        newSelectedOption = 0;
                    } else if (checkedId == binding.optionB.getId()) {
                        newSelectedOption = 1;

                    } else if (checkedId == binding.optionC.getId()) {
                        newSelectedOption = 2;


                    } else if (checkedId == binding.optionD.getId()) {
                        newSelectedOption = 3;
                    }


                    boolean isCurrentlyCorrect = answer.equals(question.getOptions().get(newSelectedOption));
                    // Update correctAnswers based on the previous and current answer status
                    boolean wasPreviouslyCorrect = previousAnswerStatus.get(question.getId());
                    if (wasPreviouslyCorrect && !isCurrentlyCorrect) {
                        // Was correct, now incorrect
                        int count = correctAnswers.getOrDefault(question.getCategory(), 0);
                        if (count > 0) {
                            correctAnswers.put(question.getCategory(), count - 1);
                        }
                    } else if (!wasPreviouslyCorrect && isCurrentlyCorrect) {
                        // Was incorrect, now correct
                        int count = correctAnswers.getOrDefault(question.getCategory(), 0);
                        correctAnswers.put(question.getCategory(), count + 1);
                    }

                    // Update the previous answer status
                    previousAnswerStatus.put(question.getId(), isCurrentlyCorrect);

                    Log.d(TAG, "Updated correctAnswers: " + correctAnswers.get(question.getCategory()));



                    question.setSelectedOption(newSelectedOption);

                    questionsMap.put(question.getId(), question);

                    callback.onAttempt(questionsMap, correctAnswers);

                }
            });


        }
    }

    public static class QuestionCategoryHolder extends RecyclerView.ViewHolder {

        String TAG = "QuestionCategoryHolder";

        ItemQuestionCategoryBinding binding;
        boolean wasCorrect;
        int itemPosition = -1;


        public QuestionCategoryHolder(@NonNull ItemQuestionCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Context context, Questions question, QuestionCallback callback, Map<Integer, Questions> questionsMap, Map<String, Integer> correctAnswers, Map<Integer, Boolean> previousAnswerStatus) {

            itemPosition=getAdapterPosition();

            String answer = question.getAnswer();
            binding.tvQuestion.setText(question.getQuestion());

            binding.optionA.setText(question.getOptions().get(0));
            binding.optionB.setText(question.getOptions().get(1));
            binding.optionC.setText(question.getOptions().get(2));
            binding.optionD.setText(question.getOptions().get(3));

            binding.radioGroup.setOnCheckedChangeListener(null);
            binding.radioGroup.clearCheck();

            int selectedPos = question.getSelectedOption();
            if (selectedPos != -1) {
                switch (selectedPos) {

                    case 0:
                        binding.optionA.setChecked(true);

                        break;
                    case 1:
                        binding.optionB.setChecked(true);

                        break;
                    case 2:
                        binding.optionC.setChecked(true);

                        break;
                    case 3:
                        binding.optionD.setChecked(true);

                        break;
                    default:

                        binding.radioGroup.clearCheck();
                }
            }

            binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {


                    int newSelectedOption = -1;

                    if (checkedId == binding.optionA.getId()) {
                        newSelectedOption = 0;
                    } else if (checkedId == binding.optionB.getId()) {
                        newSelectedOption = 1;

                    } else if (checkedId == binding.optionC.getId()) {
                        newSelectedOption = 2;


                    } else if (checkedId == binding.optionD.getId()) {
                        newSelectedOption = 3;
                    }

                    boolean isCurrentlyCorrect = answer.equals(question.getOptions().get(newSelectedOption));
                    // Update correctAnswers based on the previous and current answer status
                    boolean wasPreviouslyCorrect = previousAnswerStatus.get(question.getId());
                    if (wasPreviouslyCorrect && !isCurrentlyCorrect) {
                        // Was correct, now incorrect
                        int count = correctAnswers.getOrDefault(question.getCategory(), 0);
                        if (count > 0) {
                            correctAnswers.put(question.getCategory(), count - 1);
                        }
                    } else if (!wasPreviouslyCorrect && isCurrentlyCorrect) {
                        // Was incorrect, now correct
                        int count = correctAnswers.getOrDefault(question.getCategory(), 0);
                        correctAnswers.put(question.getCategory(), count + 1);
                    }

                    // Update the previous answer status
                    previousAnswerStatus.put(question.getId(), isCurrentlyCorrect);





                    question.setSelectedOption(newSelectedOption);

                    // Save the question in the map
                    questionsMap.put(question.getId(), question);

                    // Call the callback to notify changes
                    callback.onAttempt(questionsMap, correctAnswers);

                    Log.d(TAG, "Updated correctAnswers: " + correctAnswers.get(question.getCategory()));

                    questionsMap.put(question.getId(), question);
                    callback.onAttempt(questionsMap, correctAnswers);


                }
            });


        }
    }

    public static class AnswerHolder extends RecyclerView.ViewHolder {

        String TAG = "AnswerHolder";
        ItemAnswersBinding binding;

        public AnswerHolder(@NonNull ItemAnswersBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Context context, Questions question) {

            String answer = question.getAnswer();


            binding.tvAnswer.setText(answer + "");
            binding.tvQuestion.setText(question.getQuestion());

            binding.optionA.setText(question.getOptions().get(0));
            binding.optionB.setText(question.getOptions().get(1));
            binding.optionC.setText(question.getOptions().get(2));
            binding.optionD.setText(question.getOptions().get(3));


            resetRadioButtons(context);


            switch (question.getSelectedOption()) {
                case 0:
                    binding.optionA.setChecked(true);

                    if (answer.equals(binding.optionA.getText().toString())) {
                        binding.optionA.setButtonTintList(context.getColorStateList(R.color.green));
                    } else {
                        binding.optionA.setButtonTintList(context.getColorStateList(R.color.red));
                    }
                    break;
                case 1:
                    binding.optionB.setChecked(true);

                    if (answer.equals(binding.optionB.getText().toString())) {
                        binding.optionB.setButtonTintList(context.getColorStateList(R.color.green));
                    } else {
                        binding.optionB.setButtonTintList(context.getColorStateList(R.color.red));
                    }

                    break;
                case 2:
                    binding.optionC.setChecked(true);

                    if (answer.equals(binding.optionC.getText().toString())) {
                        binding.optionC.setButtonTintList(context.getColorStateList(R.color.green));
                    } else {
                        binding.optionC.setButtonTintList(context.getColorStateList(R.color.red));
                    }
                    break;
                case 3:
                    binding.optionD.setChecked(true);
                    if (answer.equals(binding.optionD.getText().toString())) {
                        binding.optionD.setButtonTintList(context.getColorStateList(R.color.green));
                    } else {
                        binding.optionD.setButtonTintList(context.getColorStateList(R.color.red));
                    }
                    break;
                default:
                    binding.radioGroup.clearCheck();
            }


        }

        private void resetRadioButtons(Context context) {
            // Reset the checked state and color of all RadioButtons
            binding.radioGroup.clearCheck();
            binding.optionA.setButtonTintList(context.getColorStateList(R.color.light_text_color));
            binding.optionB.setButtonTintList(context.getColorStateList(R.color.light_text_color));
            binding.optionC.setButtonTintList(context.getColorStateList(R.color.light_text_color));
            binding.optionD.setButtonTintList(context.getColorStateList(R.color.light_text_color));
        }
    }


    public interface QuestionCallback {
        void onAttempt(Map<Integer, Questions> mapQuestions, Map<String, Integer> correctAnswers);
    }

}