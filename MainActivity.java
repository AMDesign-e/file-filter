import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private Spinner spinnerFilter;
    private Button btnSelectFolder;
    private ListView listViewFiles;
    private FileAdapter fileAdapter;
    private List<FileItem> allFiles;
    private List<FileItem> filteredFiles;
    private File currentDirectory;
    
    // Filter options
    private String[] filterOptions = {
        "All Files", "Images", "Documents", "Audio", "Video", "Archives", "Custom"
    };
    
    // File extensions by category
    private final List<String> imageExtensions = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff", ".svg"
    );
    
    private final List<String> documentExtensions = Arrays.asList(
        ".pdf", ".doc", ".docx", ".txt", ".rtf", ".odt", ".xls", ".xlsx", ".ppt", ".pptx"
    );
    
    private final List<String> audioExtensions = Arrays.asList(
        ".mp3", ".wav", ".ogg", ".flac", ".m4a", ".aac", ".wma"
    );
    
    private final List<String> videoExtensions = Arrays.asList(
        ".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".webm", ".m4v"
    );
    
    private final List<String> archiveExtensions = Arrays.asList(
        ".zip", ".rar", ".7z", ".tar", ".gz", ".bz2"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupSpinner();
        setupButtonClick();
        
        // Initialize file lists
        allFiles = new ArrayList<>();
        filteredFiles = new ArrayList<>();
        
        // Setup ListView adapter
        fileAdapter = new FileAdapter(this, filteredFiles);
        listViewFiles.setAdapter(fileAdapter);
        
        // Check and request permissions
        if (checkPermissions()) {
            loadDefaultDirectory();
        } else {
            requestPermissions();
        }
    }
    
    private void initViews() {
        spinnerFilter = findViewById(R.id.spinnerFilter);
        btnSelectFolder = findViewById(R.id.btnSelectFolder);
        listViewFiles = findViewById(R.id.listViewFiles);
    }
    
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
        
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterFiles();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    
    private void setupButtonClick() {
        btnSelectFolder.setOnClickListener(v -> {
            if (checkPermissions()) {
                openFolderSelector();
            } else {
                requestPermissions();
            }
        });
    }
    
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                   == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadDefaultDirectory();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access files.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void loadDefaultDirectory() {
        currentDirectory = Environment.getExternalStorageDirectory();
        loadFilesFromDirectory(currentDirectory);
    }
    
    private void openFolderSelector() {
        // For simplicity, we'll use the default external storage
        // In a real app, you might want to use a file picker library
        loadDefaultDirectory();
    }
    
    private void loadFilesFromDirectory(File directory) {
        allFiles.clear();
        
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        String extension = getFileExtension(fileName).toLowerCase();
                        FileItem fileItem = new FileItem(
                            fileName,
                            file.getAbsolutePath(),
                            extension,
                            file.length(),
                            file.lastModified()
                        );
                        allFiles.add(fileItem);
                    }
                }
            }
        }
        
        filterFiles();
    }
    
    private void filterFiles() {
        int selectedFilter = spinnerFilter.getSelectedItemPosition();
        filteredFiles.clear();
        
        switch (selectedFilter) {
            case 0: // All Files
                filteredFiles.addAll(allFiles);
                break;
            case 1: // Images
                filterByExtensions(imageExtensions);
                break;
            case 2: // Documents
                filterByExtensions(documentExtensions);
                break;
            case 3: // Audio
                filterByExtensions(audioExtensions);
                break;
            case 4: // Video
                filterByExtensions(videoExtensions);
                break;
            case 5: // Archives
                filterByExtensions(archiveExtensions);
                break;
            case 6: // Custom - you can implement custom filtering here
                filteredFiles.addAll(allFiles);
                break;
        }
        
        fileAdapter.notifyDataSetChanged();
    }
    
    private void filterByExtensions(List<String> extensions) {
        for (FileItem file : allFiles) {
            String extension = file.getExtension().toLowerCase();
            if (extensions.contains(extension)) {
                filteredFiles.add(file);
            }
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return ""; // No extension
    }
}
