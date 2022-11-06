package com.example.diaryapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.diary.DiaryActivity;
import com.example.diaryapplication.activity.main.CustomCalenderFragment;
import com.example.diaryapplication.activity.main.SettingFragment;
import com.example.diaryapplication.service.CustomCalender.CalenderUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton createDiaryFab;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    private void init() {
        createDiaryFab = (FloatingActionButton) findViewById(R.id.createDiaryFab);

        tabs = findViewById(R.id.mainTabLayout);
        tabs.addTab(tabs.newTab().setText("메인"));
        tabs.addTab(tabs.newTab().setText("설정"));

        createDiaryFab.setOnClickListener(this);

        CustomCalenderFragment customCalenderFragment = new CustomCalenderFragment();
        SettingFragment settingFragment = new SettingFragment();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;

                switch (tab.getPosition()) {
                    case 0:
                        selected = customCalenderFragment;
                        break;
                    case 1:
                        selected = settingFragment;
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, selected).commitNow();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        downloadEmojiFileToSave();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.createDiaryFab:
                // 당일 글 작성이 되어있다면
                if (CalenderUtil.isTodayCreateMatched) {
                    Snackbar.make(view, "기존에 작성된 다이어리가 있습니다", Snackbar.LENGTH_SHORT).show();
                } else {
                    // 다이어리 액티비티로 이동
                    Intent mainIntent = new Intent(MainActivity.this, DiaryActivity.class);
                    mainIntent.putExtra("datetime", (LocalDateTime) LocalDateTime.now());
                    startActivity(mainIntent);
                }
                break;
        }
    }

    // 뒤로가기 막기
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public void downloadEmojiFileToSave() {
        // 구매한 이모티콘 파일 압축 해제
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Path source = Paths.get(download.getPath() + File.separator + "emoji.zip");
        Path target = Paths.get(getFilesDir().getAbsolutePath() + File.separator + "emoji" + File.separator);

        File file = new File(download, "emoji.zip");

        // 파일 존재 여부 확인
        if (file.exists()) {
            Log.i("test", "다운로드 폴더에서 파일 존재함");

            unzipFile(source, target);

            // 파일 삭제
            try {
                Files.delete(source);
            } catch (IOException e) {
                e.printStackTrace();
            }

            moveFile();
        }
    }

    public void moveFile() {
        // 파일 이동
        String path = getFilesDir().getAbsolutePath() + File.separator + "emoji" + File.separator + "images";

        File diretory = new File(path);

        File[] files = diretory.listFiles();

        List<String> fileNameList = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            fileNameList.add(files[i].getName());

            Path fromPath = Paths.get(path + File.separator + files[i].getName());

            Path toPath = Paths.get(getFilesDir().getAbsolutePath() + File.separator + "emoji" + File.separator + files[i].getName());;

            try {
                Files.move(fromPath, toPath);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 파일 삭제
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unzipFile(Path sourceZip, Path targetDir) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZip.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                boolean isDirectory = false;
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, targetDir);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    // copy files
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        // test zip slip vulnerability
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }
        return normalizePath;
    }
}