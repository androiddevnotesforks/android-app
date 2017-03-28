package jcotter.listenmoe.model;

public class PlaybackInfo extends BasicTrack {
    private int songId;
    private String animeName;
    private String requestedBy;
    private int listeners;
    private BasicTrack last;
    private BasicTrack secondLast;
    private ExtendedInfo extended;

    public PlaybackInfo(String artistName, String songName, int songId, String animeName, String requestedBy, int listeners, BasicTrack last, BasicTrack secondLast) {
        super(artistName, songName);
        this.songId = songId;
        this.animeName = animeName;
        this.requestedBy = requestedBy;
        this.listeners = listeners;
        this.last = last;
        this.secondLast = secondLast;
    }

    public PlaybackInfo(String artistName, String songName, int songId, String animeName, String requestedBy, int listeners, BasicTrack last, BasicTrack secondLast, ExtendedInfo extended) {
        super(artistName, songName);
        this.songId = songId;
        this.animeName = animeName;
        this.requestedBy = requestedBy;
        this.listeners = listeners;
        this.last = last;
        this.secondLast = secondLast;
        this.extended = extended;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public BasicTrack getLast() {
        return last;
    }

    public void setLast(BasicTrack last) {
        this.last = last;
    }

    public BasicTrack getSecondLast() {
        return secondLast;
    }

    public void setSecondLast(BasicTrack secondLast) {
        this.secondLast = secondLast;
    }

    public ExtendedInfo getExtended() {
        return extended;
    }

    public void setExtended(ExtendedInfo extended) {
        this.extended = extended;
    }

    public boolean hasExtended() {
        return this.extended != null;
    }
}
