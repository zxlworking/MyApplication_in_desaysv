package com.dsv.test_tiff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.tiff.TiffDataFormat;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.img);

        String mFilePath="/storage/sdcard0/zxl_test_1/tiff_t/oxford.tif";
        try {
            Metadata metadata = TiffMetadataReader.readMetadata(new File(mFilePath));
            Iterable<Directory> mDirectories = metadata.getDirectories();
            for(Directory directory : mDirectories){
                Iterator<Tag> tag = directory.getTags().iterator();
                while (tag.hasNext()) {
                    Tag mTag = tag.next();
                    System.out.println("zxl--->"+mTag+"--->"+mTag.getTagName()+"--->"+mTag.getTagType());
                    if(directory.containsTag(mTag.getTagType())){
                        System.out.println("zxl--->data--->"+directory.getString(mTag.getTagType()));
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TiffProcessingException e) {
            e.printStackTrace();
        }
    }
}
