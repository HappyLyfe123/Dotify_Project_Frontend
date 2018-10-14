package com.example.thai.dotify;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Server.DotifySong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class SongsListFragment extends Fragment{

    private ImageButton backButton;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;
    private List<Song> songsList = new ArrayList<>();
    private SongsAdapter songsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;


    private static String playListTitle;
    private String username;
    private Context activityContext;

    public SongsListFragment(){

    }

    /**
     * Listener to tell the main container to switch fragments
     */
    public interface OnChangeFragmentListener{
        void songClicked(MainActivityContainer.PlaylistFragmentType fragmentType,
                         PlayingMusicController playingMusicController);
    }

    /**
     * Sets the OnChangeFragmentLIstener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener){
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /***
     * create View object of fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object of type SongsListFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        backButton = (ImageButton) view.findViewById(R.id.song_list_back_image_button);
        backButton.setOnClickListener((backButtonView) -> {
            getFragmentManager().popBackStackImmediate();
        });
        titleTextView = (TextView) view.findViewById(R.id.song_list_title_text_view);
        songListRecycleView = (RecyclerView) view.findViewById(R.id.song_list_recycle_view);
        SharedPreferences sharedPreferences = activityContext.getSharedPreferences("UserData", MODE_PRIVATE);
        username = "PenguinDan";//sharedPreferences.getString("username", null);


        // Initialize the recycler view listener
        RecyclerViewClickListener songItemClickListener = (listView, position) -> {
            // Create a music controller object
            PlayingMusicController musicController = new PlayingMusicController(songsList);
            // Set the current song to be played for the controller
            musicController.setCurrentSong(position);
            // Tell the main activity
            onChangeFragmentListener.songClicked(
                    MainActivityContainer.PlaylistFragmentType.FULL_SCREEN_MUSIC,
                    musicController);
        };
        //Display all of the items into the recycler view
        songsAdapter = new SongsAdapter(songsList, songItemClickListener);
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        songListRecycleView.setLayoutManager(songLayoutManager);
        songListRecycleView.setItemAnimator(new DefaultItemAnimator());
        songListRecycleView.setAdapter(songsAdapter);
        getSongList();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFragmentTitle();
    }

    //Set the title for the current fragment to playlist name
    public static void setPlayListTitle(String title){
        playListTitle = title;
    }

    //Set the title for the current fragment to playlist name
    private void setFragmentTitle(){
        titleTextView.setText(playListTitle);
    }


    private void getSongList(){
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));

        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);

        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<JSONObject> getSongList = dotifyHttpInterface.getSongList(
                getString(R.string.appKey),
                username,
                playListTitle
        );

        getSongList.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, retrofit2.Response<JSONObject> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    JSONObject mySong = response.body();
                    try {
                        System.out.println(mySong.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //System.out.println(mySongList.get(2).getTitle());

                    //Converts the playlist we got to a list of playlists instead of a list of strings

                } else {
                    //If unsucessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retreive playlists " + response.code());
                }
            }


            //If something is wrong with our request to the server, goes to this method
            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });
    }
}
