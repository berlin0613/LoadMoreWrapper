package com.github.nukc.sample;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.nukc.LoadMoreWrapper.LoadMoreAdapter;
import com.github.nukc.LoadMoreWrapper.LoadMoreWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * A simple {@link Fragment} subclass.
 */
public class SampleFragment extends Fragment {

    private static final String TAG = SampleFragment.class.getSimpleName();
    private static final String ARGS_COUNT = "count";
    private static final String ARGS_MANAGER_MODE = "managerMode";

    public static final int MODE_LINEARLAYOUT = 1;
    public static final int MODE_GRIDLAYOUT = 2;
    public static final int MODE_STAGGEREDGRIDLAYOUT = 3;

    @IntDef({MODE_LINEARLAYOUT, MODE_GRIDLAYOUT, MODE_STAGGEREDGRIDLAYOUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ManagerMode{}

    private SampleAdapter mSampleAdapter;
    // use for demo, please ignore
    private boolean mShowLoadFailedEnabled = true;

    public static SampleFragment newInstance(int count, @ManagerMode int managerMode) {
        Bundle args = new Bundle();
        args.putInt(ARGS_COUNT, count);
        args.putInt(ARGS_MANAGER_MODE, managerMode);

        SampleFragment fragment = new SampleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SampleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int count = getArguments().getInt(ARGS_COUNT, 0);
        mSampleAdapter = new SampleAdapter(count);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        int managerMode = getArguments().getInt(ARGS_MANAGER_MODE);

        switch (managerMode) {
            case MODE_LINEARLAYOUT:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                break;
            case MODE_GRIDLAYOUT:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
                break;
            case MODE_STAGGEREDGRIDLAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager =
                        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                break;
            default:
                throw new IllegalArgumentException();
        }


//        LoadMoreAdapter recyclerAdapter = new LoadMoreAdapter(mSampleAdapter);
//        recyclerView.setAdapter(recyclerAdapter);
//        recyclerAdapter.setLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
//                //not enable load more
//                if (mSampleAdapter.getItemCount() >= 40) {
//                    enabled.setLoadMoreEnabled(false);
//                }
//
//                recyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSampleAdapter.addItem();
//                    }
//                }, 1200);
//            }
//        });
        LoadMoreWrapper.with(mSampleAdapter)
                .setFooterView(managerMode == MODE_GRIDLAYOUT ? R.layout.view_footer : -1)
//                .setLoadMoreEnabled(false)
                .setShowNoMoreEnabled(true)
                .setListener(new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(final LoadMoreAdapter.Enabled enabled) {
                        int itemCount = mSampleAdapter.getItemCount();
                        if (itemCount > 20 && mShowLoadFailedEnabled) {
                            mShowLoadFailedEnabled = false;
                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    enabled.setLoadFailed(true);
                                }
                            }, 800);
                        } else {
                            //not enable load more
                            if (itemCount >= 40) {
                                enabled.setLoadMoreEnabled(false);
                            }

                            recyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mSampleAdapter.addItem();
                                }
                            }, 1200);
                        }
                    }
                })
                .into(recyclerView);

        return view;
    }

    public static class SampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int mCount;

        public SampleAdapter(int count) {
            mCount = count;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
            return new SampleHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((SampleHolder) holder).mTextView.setText(position + "");
        }

        @Override
        public int getItemCount() {
            return mCount;
        }

        public void addItem() {
            final int positionStart = mCount;
            mCount+= 5;
            notifyItemRangeInserted(positionStart, 5);
        }

        static class SampleHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            public SampleHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
