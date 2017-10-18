package com.puzzletrivia.animalmathgames;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final int QUESTIONCOUNT = 3;

    class questionStructure {
        public String question;

        public boolean questionWithImage;
        public int questionImageID;

        public String correctAnswer;
        public String wrongAnswer1;
        public String wrongAnswer2;

        public boolean answerWithImage;
        public int correctImageID;
        public int wrongImageID1;
        public int wrongImageID2;

        questionStructure(){
            question = "";
            questionWithImage = false;
            questionImageID = 0;
            correctAnswer = "";
            wrongAnswer1 = "";
            wrongAnswer2 = "";
            answerWithImage = false;
            correctImageID = 0;
            wrongImageID1 = 0;
            wrongImageID2 = 0;

        }
    }

    int categoryNumber;
    questionStructure[] questionData;
    List<questionStructure> allQuestionData;

    TextView questionTextOnly;
    TextView questionText;
    ImageView questionImage;
    LinearLayout textAnswerLayout;
    Button answerText1;
    Button answerText2;
    Button answerText3;
    LinearLayout imageAnswerLayout;
    ImageButton answerImage1;
    ImageButton answerImage2;
    ImageButton answerImage3;

    int questionNumber;
    int[] answerOrder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        questionTextOnly = (TextView) findViewById(R.id.questionTextOnly);
        questionText = (TextView) findViewById(R.id.questionText);
        questionImage = (ImageView) findViewById(R.id.questionImage);
        textAnswerLayout = (LinearLayout) findViewById(R.id.textAnswer);
        answerText1 = (Button) findViewById(R.id.answerText1);
        answerText2 = (Button) findViewById(R.id.answerText2);
        answerText3 = (Button) findViewById(R.id.answerText3);
        imageAnswerLayout = (LinearLayout) findViewById(R.id.imageAnswer);
        answerImage1 = (ImageButton) findViewById(R.id.answerImage1);
        answerImage2 = (ImageButton) findViewById(R.id.answerImage2);
        answerImage3 = (ImageButton) findViewById(R.id.answerImage3);



        //get category id
        categoryNumber = getIntent().getIntExtra("CategoryNumber",1);


        //get all question data from textfile
         allQuestionData = readTextFileAsList(getApplicationContext(),getApplicationContext().getResources().getIdentifier("category"+categoryNumber,"raw", getApplicationContext().getPackageName()));

        //randomize question data
        Collections.shuffle(allQuestionData);

        //pick the first n question from all randomize question
        questionData = new questionStructure[QUESTIONCOUNT];
        for (int i = 0; i < questionData.length; i++){
            questionData[i] = allQuestionData.get(i);
        }

        //indicator to help randomize the order of answer. 0 mean the correct answer. 1 and 2 mean wrong answer.
        answerOrder = new int[]{0,1,2};
        answerOrder = fisherYatesShuffle(answerOrder);

        questionNumber = 0;
        loadQuestion(questionData[questionNumber]);



    }


    private List<questionStructure> readTextFileAsList(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader bufferedreader = new BufferedReader(inputreader);

        List<questionStructure> list = new ArrayList<>();
        questionStructure currentQuestion = new questionStructure();

        String line;
        int lineQuestionIndex = 1;

        try
        {
            while (( line = bufferedreader.readLine()) != null)
            {
                switch (lineQuestionIndex){
                    case 1:
                        currentQuestion.question = line;
                        break;
                    case 2:
                        if (!line.equals("noimage")){
                            currentQuestion.questionWithImage = true;
                            currentQuestion.questionImageID = ctx.getResources().getIdentifier(line,"drawable", ctx.getPackageName());
                        }
                        break;
                    case 3:
                        if (line.equals("image")){
                            currentQuestion.answerWithImage = true;
                        }
                        break;
                    case 4:
                        if (currentQuestion.answerWithImage){
                            currentQuestion.correctImageID = ctx.getResources().getIdentifier(line,"drawable", ctx.getPackageName());
                        }else{
                            currentQuestion.correctAnswer = line;
                        }
                        break;
                    case 5:
                        if (currentQuestion.answerWithImage){
                            currentQuestion.wrongImageID1 = ctx.getResources().getIdentifier(line,"drawable", ctx.getPackageName());
                        }else{
                            currentQuestion.wrongAnswer1 = line;
                        }
                        break;
                    case 6:
                        if (currentQuestion.answerWithImage){
                            currentQuestion.wrongImageID2 = ctx.getResources().getIdentifier(line,"drawable", ctx.getPackageName());
                        }else{
                            currentQuestion.wrongAnswer2 = line;
                        }
                        break;
                    default:
                        lineQuestionIndex = 0;
                        list.add(currentQuestion);
                        currentQuestion = new questionStructure();
                        break;
                }
                lineQuestionIndex ++;
            }
        }
        catch (IOException e)
        {
            return null;
        }
        return list;
    }

    private int[] fisherYatesShuffle(int[] array) {
        int n = array.length;
        int randomValue;
        int randomElement;

        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            randomValue = i + random.nextInt(n - i);
            randomElement = array[randomValue];
            array[randomValue] = array[i];
            array[i] = randomElement;
        }

        return array;
    }

    private void loadQuestion(questionStructure currentQuestionData){

        //set question
        if (currentQuestionData.questionWithImage){
            questionTextOnly.setVisibility(View.GONE);
            questionText.setVisibility(View.VISIBLE);
            questionImage.setVisibility(View.VISIBLE);

            questionText.setText(currentQuestionData.question.replace("\\n", "\n"));
            questionImage.setImageResource(currentQuestionData.questionImageID);
        }else{
            questionText.setVisibility(View.GONE);
            questionImage.setVisibility(View.GONE);
            questionTextOnly.setVisibility(View.VISIBLE);

            questionTextOnly.setText(currentQuestionData.question.replace("\\n", "\n"));
        }

        //set answer
        if (currentQuestionData.answerWithImage){
            textAnswerLayout.setVisibility(View.GONE);
            imageAnswerLayout.setVisibility(View.VISIBLE);

            answerImage1.setVisibility(View.VISIBLE);
            answerImage2.setVisibility(View.VISIBLE);
            answerImage3.setVisibility(View.VISIBLE);

            answerImage1.setImageResource(getAnswerImage(answerOrder[0],currentQuestionData));
            answerImage2.setImageResource(getAnswerImage(answerOrder[1],currentQuestionData));
            answerImage3.setImageResource(getAnswerImage(answerOrder[2],currentQuestionData));
        }else {
            imageAnswerLayout.setVisibility(View.GONE);
            textAnswerLayout.setVisibility(View.VISIBLE);

            answerText1.setVisibility(View.VISIBLE);
            answerText2.setVisibility(View.VISIBLE);
            answerText3.setVisibility(View.VISIBLE);

            answerText1.setText(getAnswerText(answerOrder[0],currentQuestionData));
            answerText2.setText(getAnswerText(answerOrder[1],currentQuestionData));
            answerText3.setText(getAnswerText(answerOrder[2],currentQuestionData));
        }
    }

    private int getAnswerImage(int indicator, questionStructure currentQuestionData){
        int imageID;
        switch (indicator){
            case 0:
                imageID = currentQuestionData.correctImageID;
            break;
            case 1:
                imageID = currentQuestionData.wrongImageID1;
            break;
            case 2:
                imageID = currentQuestionData.wrongImageID2;
            break;
            default:
                imageID = 0;
            break;
        }
        return imageID;
    }

    private String getAnswerText(int indicator, questionStructure currentQuestionData){
        String text;
        switch (indicator){
            case 0:
                text = currentQuestionData.correctAnswer;
                break;
            case 1:
                text = currentQuestionData.wrongAnswer1;
                break;
            case 2:
                text = currentQuestionData.wrongAnswer2;
                break;
            default:
                text = "";
                break;
        }
        return text;
    }

    public void clickAnswer(View view){
        int buttonID = view.getId();
        int indicator;
        switch (buttonID){
            case R.id.answerText1:
            case R.id.answerImage1:
                indicator = answerOrder[0];
                break;
            case R.id.answerText2:
            case R.id.answerImage2:
                indicator = answerOrder[1];
                break;
            case R.id.answerText3:
            case R.id.answerImage3:
                indicator = answerOrder[2];
                break;
            default:
                indicator = -1;
                break;
        }

        if (indicator==0){
            questionNumber = questionNumber + 1;
            if (questionNumber < QUESTIONCOUNT){
                ImageView questionNumberImage = (ImageView) findViewById(getApplicationContext().getResources().getIdentifier("questionNumber"+questionNumber,"id", getApplicationContext().getPackageName()));
                questionNumberImage.setImageResource(R.drawable.game_question_number_completed);
                loadQuestion(questionData[questionNumber]);
            }else{
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }


        }else{
            view.setVisibility(View.GONE);
        }

    }

}


