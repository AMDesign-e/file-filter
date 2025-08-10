import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileAdapter extends BaseAdapter {
    private Context context;
    private List<FileItem> fileList;
    private LayoutInflater inflater;

    public FileAdapter(Context context, List<FileItem> fileList) {
        this.context = context;
        this.fileList = fileList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_file, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageViewFileIcon);
            holder.textViewName = convertView.findViewById(R.id.textViewFileName);
            holder.textViewDetails = convertView.findViewById(R.id.textViewFileDetails);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileItem fileItem = fileList.get(position);
        
        holder.textViewName.setText(fileItem.getName());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date(fileItem.getLastModified()));
        String size = formatFileSize(fileItem.getSize());
        
        holder.textViewDetails.setText(fileItem.getExtension() + " • " + size + " • " + date);

        return convertView;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewDetails;
    }
}
