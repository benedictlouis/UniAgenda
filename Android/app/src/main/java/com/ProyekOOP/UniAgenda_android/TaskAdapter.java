package com.ProyekOOP.UniAgenda_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ProyekOOP.UniAgenda_android.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private List<Task> filteredTaskList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.filteredTaskList = new ArrayList<>(taskList);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = filteredTaskList.get(position);
        holder.taskTitle.setText(task.getTask_title());
        holder.taskDeadline.setText(task.getTask_deadline());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getTask_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredTaskList.size();
    }

    public void filterByDeadline(String deadline) {
        filteredTaskList.clear();
        for (Task task : taskList) {
            try {
                Date taskDate = dateFormat.parse(task.getTask_deadline());
                Date filterDate = dateFormat.parse(deadline);
                if (taskDate != null && filterDate != null && taskDate.equals(filterDate)) {
                    filteredTaskList.add(task);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void filterByTitle(String query) {
        filteredTaskList.clear();
        if (query.isEmpty()) {
            filteredTaskList.addAll(taskList);
        } else {
            for (Task task : taskList) {
                if (task.getTask_title().toLowerCase().contains(query.toLowerCase())) {
                    filteredTaskList.add(task);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle, taskDeadline;

        public TaskViewHolder(View view) {
            super(view);
            taskTitle = view.findViewById(R.id.task_title);
            taskDeadline = view.findViewById(R.id.task_deadline);
        }
    }
}
