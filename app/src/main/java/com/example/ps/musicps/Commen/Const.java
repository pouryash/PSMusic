package com.example.ps.musicps.Commen;

public class Const {

//    public ImageView playingSongImage;
//    public TextView playingSongName;
//    public TextView playingSongArtist;
//    public ImageView playPauseButtonPlayingSong;
//    public RelativeLayout playingSongRoot;
//    public static Const instance;
//
//
//    private Const(){
//
//    }
//
//    static {
//        instance = new Const();
//    }
//
//    public static Const getInstance(){
//        return instance;
//    }
//    public void setupPlayingSong(Song song, MediaPlayer mediaPlayer , Context context) {
//
//        Glide.with(context).asBitmap().load(Uri.parse(song.getSongImageUri()))
//                .apply(new RequestOptions().placeholder(R.drawable.no_image))
//                .into(playingSongImage);
//        if (mediaPlayer != null) {
//            try {
//                if (mediaPlayer.isPlaying()) {
//                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_pause_24px, null));
//                } else {
//                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_play_24px, null));
//                }
//            }catch (IllegalStateException e){
//                Toast.makeText(context,e.getCause()+"1111",Toast.LENGTH_LONG).show();
//            }
//        } else {
//            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_play_24px, null));
//        }
//        playingSongArtist.setText(song.getArtistName());
//        playingSongName.setText(song.getSongName());
//
//    }
//


}
