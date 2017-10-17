package com.puzzletrivia.animalmathgames;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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


    private int categoryNumber;
    questionStructure[] questionData;
    List<questionStructure> allQuestionData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        //get category id
        categoryNumber = getIntent().getIntExtra("CategoryNumber",1);

        //create array of question data
        questionData = new questionStructure[3];
        for (int i = 0; i < questionData.length; i++){
            questionData[i] = new questionStructure();
        }


         allQuestionData = readTextFileAsList(getApplicationContext(),R.raw.category1);
        for (int i = 0; i< allQuestionData.size();i++){
            Log.d("soalnya", "adalah: "+allQuestionData.get(i).question);
        }



    }


    public List<questionStructure> readTextFileAsList(Context ctx, int resId)
    {
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
                        currentQuestion.answerWithImage = true;
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
}


