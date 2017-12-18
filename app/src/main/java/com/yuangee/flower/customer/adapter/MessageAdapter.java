package com.yuangee.flower.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuangee.flower.customer.ApiManager;
import com.yuangee.flower.customer.App;
import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.entity.Message;
import com.yuangee.flower.customer.network.HttpResultFunc;
import com.yuangee.flower.customer.network.MySubscriber;
import com.yuangee.flower.customer.network.NoErrSubscriberListener;
import com.yuangee.flower.customer.util.RxManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/6 0006.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

    private List<Message> messages;

    private Context context;

    private RxManager rxManager;

    public MessageAdapter(Context context, RxManager rxManager) {
        this.context = context;
        this.rxManager = rxManager;
        messages = new ArrayList<>();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, null);
        Holder holder = new Holder(view);
        holder.rootView = view;
        holder.icRead = view.findViewById(R.id.ic_read);
        holder.messageText = view.findViewById(R.id.message_text);
        holder.textDate = view.findViewById(R.id.text_date);
        holder.titleText = view.findViewById(R.id.title_text);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        final Message msg = messages.get(position);
        holder.textDate.setText(msg.created);
        holder.messageText.setText(msg.contentMore);
        holder.titleText.setText(msg.content);
        holder.icRead.setVisibility(msg.noticeRead.hasRead ? View.GONE : View.VISIBLE);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!msg.noticeRead.hasRead) {
                    readMsg(msg.id, position);
                }
            }
        });
    }

    private void readMsg(long id, final int position) {
        Observable<Object> observable = ApiManager.getInstance().api
                .readMemberNotice(App.getPassengerId(), id)
                .map(new HttpResultFunc<>(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(context, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                Message msg = messages.get(position);
                msg.noticeRead.hasRead = true;
                notifyDataSetChanged();
            }
        })));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView textDate;

        ImageView icRead;

        TextView messageText;

        TextView titleText;

        View rootView;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }
}
