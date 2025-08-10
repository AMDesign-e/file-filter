public class FileItem {
    private String name;
    private String path;
    private String extension;
    private long size;
    private long lastModified;

    public FileItem(String name, String path, String extension, long size, long lastModified) {
        this.name = name;
        this.path = path;
        this.extension = extension;
        this.size = size;
        this.lastModified = lastModified;
    }

    // Getters
    public String getName() { return name; }
    public String getPath() { return path; }
    public String getExtension() { return extension; }
    public long getSize() { return size; }
    public long getLastModified() { return lastModified; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPath(String path) { this.path = path; }
    public void setExtension(String extension) { this.extension = extension; }
    public void setSize(long size) { this.size = size; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
}
