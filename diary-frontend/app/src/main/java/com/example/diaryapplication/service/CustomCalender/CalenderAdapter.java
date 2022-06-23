package com.example.diaryapplication.service.CustomCalender;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.diary.DiaryActivity;
import com.example.diaryapplication.dto.BoardResult;
import com.example.diaryapplication.dto.EmotionFileResult;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.CalenderViewHolder> {

    ArrayList<LocalDate> dayList;

    ArrayList<BoardResult> listBoardResult;

    public CalenderAdapter(ArrayList<LocalDate> dayList, ArrayList<BoardResult> listBoardResult) {
        this.dayList = dayList;
        this.listBoardResult = listBoardResult;
    }

    @NonNull
    @Override
    public CalenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.recyclerview_calender_row, parent, false);

        return new CalenderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderViewHolder holder, int position) {
        // 날짜 변수에 담기
        LocalDate day = dayList.get(position);

        if (day == null) {
            holder.dayText.setText("");
        } else {
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));

            // 오늘 날짜 색상 칠하기
            if (day.equals(CalenderUtil.selectedDate) &&
                    LocalDate.now().getMonthValue() == CalenderUtil.selectedDate.getMonthValue() &&
                    LocalDate.now().getYear() == CalenderUtil.selectedDate.getYear()
            ) {
                holder.parentView.setBackgroundColor(Color.LTGRAY);
            }
        }

        // 날짜에 이모티콘 표시
        if (!listBoardResult.isEmpty()) {
            for (int cnt = 0; cnt < listBoardResult.size(); cnt++) {
                int date = listBoardResult.get(cnt).getDatetime().getDate();

                if (date == day.getDayOfMonth()) {
                    // 날짜 비활성화
                    holder.dayText.setVisibility(View.INVISIBLE);
                    // 이미지 넣기
                    ArrayList<EmotionFileResult> imagePath = listBoardResult.get(cnt).getEmotion_files();

                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath.get(0).getImage_file_path());
                    holder.dayImageView.setImageBitmap(bitmap);
                }
            }
        }

        // 텍스트 색상 지정(토요일, 일요일)
        if ((position + 1) % 7 == 0) {
            // 토요일 파랑
            holder.dayText.setTextColor(Color.BLUE);
        } else if (position == 0 || position % 7 == 0) {
            // 일요일 빨강
            holder.dayText.setTextColor(Color.RED);
        }

        // 날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nowDay = LocalDate.now().getDayOfMonth();
                int positionDay = position + 1;

                if (positionDay <= nowDay) {
                    Intent intent = new Intent(holder.itemView.getContext(), DiaryActivity.class);

                    for (int cnt = 0; cnt < listBoardResult.size(); cnt++) {
                        int date = listBoardResult.get(cnt).getDatetime().getDate();
                        BoardResult boardResult = listBoardResult.get(cnt);

                        // 글 작성이 되어있는지 체크
                        if (positionDay == date) {
                            // 디테일 화면으로 이동
                            intent.putExtra("isWrite", true);
                            intent.putExtra("boardResult", boardResult);
                        }
                    }
                    // 글 작성 액티비티로 이동
                    intent.putExtra("datetime", (LocalDateTime) day.atTime(LocalTime.now()));
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalenderViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        View parentView;

        ImageView dayImageView;

        public CalenderViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);

            parentView = itemView.findViewById(R.id.parentView);

            dayImageView = itemView.findViewById(R.id.dayImageView);
        }
    }
}
