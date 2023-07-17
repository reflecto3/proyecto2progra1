public class Track {
    public final String mp3File, csvFile, beginFile, endFile;

    public Track(String mp3File, String csvFile, String beginFile, String endFile) {
        this.mp3File = mp3File;
        this.csvFile = csvFile;
        this.beginFile = beginFile;
        this.endFile = endFile;
    }

    public Track(String name) {
        this.mp3File = name + ".mp3";
        this.csvFile = name + ".csv";
        this.beginFile = name + ".begin.txt";
        this.endFile = name + ".end.txt";
    }
}
