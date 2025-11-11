package com.example.fuportal.core.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fuportal.R;
import com.example.fuportal.core.data.model.UserDetail;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserDetail> userList;
    private OnUserActionsListener listener;

    public interface OnUserActionsListener {
        void onEditClick(UserDetail user);
        void onDeleteClick(UserDetail user);
    }

    public UserAdapter(List<UserDetail> userList, OnUserActionsListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDetail user = userList.get(position);

        String campusInfo = user.campusName != null ? user.campusName : "System Admin";

        holder.tvUserName.setText(user.fullName + " (" + user.userCode + ")");
        holder.tvUserRole.setText(user.roleName + " - " + campusInfo);

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(user));
        holder.btnDelete.setText(user.isActive ? "Deactivate" : "Reactivate");
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    public void setUserList(List<UserDetail> newUserDetails) {
        this.userList = newUserDetails;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserRole;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}