package com.puzzletrivia.animalmathgames;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CategoryActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private AdView mAdView;

    private ViewPager mViewPager;

    private ImageButton muteON;
    private ImageButton muteOFF;
    private ImageButton share;


    public static MediaPlayer SETap;
    public static MediaPlayer SEFail;
    public static MediaPlayer BGM;

    public static boolean preventPause;
    public static boolean mute;

    public static View[] categoryView;

    public static SharedPreferences pref;
    public SharedPreferences.Editor editor;

    private static final int CATEGORYCOUNT = 10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_category);

        //load ads
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        categoryView = new View[CATEGORYCOUNT];
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        //editor.clear();
        editor.putBoolean("finishedCategory"+1, true);
        editor.commit();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        int padding = (int) (0.2 * getResources().getDisplayMetrics().widthPixels);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(padding, 0, padding, 0);

        muteON = (ImageButton)findViewById(R.id.muteON);
        muteOFF = (ImageButton)findViewById(R.id.muteOFF);
        share = (ImageButton)findViewById(R.id.share);

        // mute ON
        muteON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BGM.pause();
                mute = true;
                muteON.setVisibility(View.GONE);
                muteOFF.setVisibility(View.VISIBLE);
            }
        });

        // mute OFF
        muteOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BGM.start();
                mute = false;
                muteOFF.setVisibility(View.GONE);
                muteON.setVisibility(View.VISIBLE);

            }
        });

        // share
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Try this apps!\n https://play.google.com/store/apps/details?id=com.puzzleanimal.alphabet";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try this apps");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        SETap = MediaPlayer.create(this, R.raw.sound_tap);
        SEFail = MediaPlayer.create(this, R.raw.sound_fail);
        BGM = MediaPlayer.create(this, R.raw.sound_bgm);
        BGM.setLooping(true);
        preventPause = false;

    }

    public static void playSoundTap(){
        SETap.start();
    }

    public static void playSoundFail(){
        SEFail.start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        BGM.release();
        SETap.release();
        SEFail.release();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (!preventPause){
            BGM.pause();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (BGM != null && !BGM.isPlaying() && !mute){
            BGM.start();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (resultCode == RESULT_OK) {
            int categoryFinished = data.getIntExtra("categoryIndexFinished",1);
            if (!pref.getBoolean("finishedCategory"+(categoryFinished+1), false) && categoryFinished<10){
                View imageButton = categoryView[categoryFinished].findViewById(R.id.categoryImage);
                imageButton.setEnabled(true);
                imageButton = (ImageButton) categoryView[categoryFinished].findViewById(R.id.lock);
                imageButton.setVisibility(View.GONE);

                editor.putBoolean("finishedCategory"+(categoryFinished+1), true);
                editor.commit();

            }
            mViewPager.arrowScroll(View.FOCUS_RIGHT);
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";



        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_category, container, false);
            final int categoryNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.categoryImage);
            ImageButton lock = (ImageButton) rootView.findViewById(R.id.lock);
            final Context context = getContext();
            int id = context.getResources().getIdentifier("category_"+categoryNumber, "drawable", context.getPackageName());
            imageButton.setImageResource(id);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playSoundTap();
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    intent.putExtra("CategoryNumber",categoryNumber);
                    preventPause = true;
                    startActivityForResult(intent,0);
                }
            });

            lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playSoundFail();
                    Toast.makeText(getActivity(), "Finish previous category first!",
                            Toast.LENGTH_SHORT).show();

                }
            });

            if (pref.getBoolean("finishedCategory"+categoryNumber, false)){
                lock.setVisibility(View.GONE);
            }else{
                imageButton.setEnabled(false);
            }

            categoryView[categoryNumber-1] = rootView;


            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return CATEGORYCOUNT;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
