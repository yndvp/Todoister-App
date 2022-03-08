package com.bawp.todoister;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private EditText enterTodo;
    private ImageButton calenderButton;
    private ImageButton priorityButton;
    private ImageButton saveButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private CalendarView calendarView;
    private Group calenderGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;

    public BottomSheetFragment(){

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calenderGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calenderButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(sharedViewModel.getSelectedItem().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();

            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calenderGroup.setVisibility(
                        calenderGroup.getVisibility() == View.GONE ?
                        View.VISIBLE : View.GONE);
                Utils.hideSoftKeyboard(view);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
                dueDate = calendar.getTime();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = enterTodo.getText().toString().trim();
                if(!TextUtils.isEmpty(task) && dueDate != null) {
                    Task myTask = new Task(task, Priority.HIGH,
                            dueDate, Calendar.getInstance().getTime(),
                            false);

                    if(isEdit) {
                        Task updatedTask = sharedViewModel.getSelectedItem().getValue();
                        updatedTask.setTask(task);
                        updatedTask.setDateCreated(Calendar.getInstance().getTime());
                        updatedTask.setPriority(Priority.HIGH);
                        updatedTask.setDueDate(dueDate);

                        TaskViewModel.update(updatedTask);
                        sharedViewModel.setIsEdit(false);
                    } else {
                        TaskViewModel.insert(myTask);
                    }
                    enterTodo.setText("");

                    if(BottomSheetFragment.this.isVisible()) {
                        BottomSheetFragment.this.dismiss();
                    }
                } else {
                    Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.today_chip) {
            // set date for today
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calendar.getTime();
        } else if(id == R.id.tomorrow_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calendar.getTime();
        } else if(id == R.id.next_week_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            dueDate = calendar.getTime();
        }
    }
}