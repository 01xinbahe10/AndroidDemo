package hxb.xb_testandroidfunction.test_different_display.presentation;

public class VideoVO {
    public static class MovieListVO{
        //电影名字
        private String movieTitle = "";
        //简介
        private String movieIntroduction = "";

        //url
        private String url = "";

        public String getMovieTitle() {
            return movieTitle;
        }

        public void setMovieTitle(String movieTitle) {
            this.movieTitle = movieTitle;
        }

        public String getMovieIntroduction() {
            return movieIntroduction;
        }

        public void setMovieIntroduction(String movieIntroduction) {
            this.movieIntroduction = movieIntroduction;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class AppListVO{
        private int appIcon = -1;

        public int getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(int appIcon) {
            this.appIcon = appIcon;
        }
    }
}
