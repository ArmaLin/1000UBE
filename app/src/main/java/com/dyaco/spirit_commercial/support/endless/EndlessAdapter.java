package com.dyaco.spirit_commercial.support.endless;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


@SuppressWarnings("unchecked")
public abstract class EndlessAdapter<LVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    private final int TYPE_LOAD_MORE = 101;
    private boolean loading;
    private View loadMoreView;

    public EndlessAdapter(View loadMoreView) {
        this.loadMoreView = loadMoreView;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (loading && viewType == TYPE_LOAD_MORE) {
            return new LoadMoreHolder(loadMoreView);
        }
        return onCreateHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //noinspection StatementWithEmptyBody
        if (loading && getItemViewType(position) == TYPE_LOAD_MORE) {
        } else {
            onBindHolder((LVH) holder, position);
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyDataSetChanged();
    }

    public boolean isLoading() {
        return loading;
    }

    @Override
    public final int getItemViewType(int position) {
        if (loading && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        return getViewType(position);
    }

    @Override
    public final int getItemCount() {

        if (loading) {
            return getCount() + 1;
        } else {
            return getCount();
        }
    }

    public abstract int getViewType(int position);

    public abstract int getCount();

    public abstract LVH onCreateHolder(ViewGroup parent, int viewType);

    public abstract void onBindHolder(LVH holder, int position);

    public static EndlessAdapter wrap(final RecyclerView.Adapter adapter, View loadMoreView) {
        if (adapter instanceof EndlessAdapter) {
            return (EndlessAdapter) adapter;
        }
        return new EndlessAdapter(loadMoreView) {
            @Override
            public int getViewType(int position) {
                return adapter.getItemViewType(position);
            }

            @Override
            public int getCount() {
                return adapter.getItemCount();
            }

            @Override
            public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
                return adapter.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
                adapter.onBindViewHolder(holder, position);
            }

            @Override
            public long getItemId(int position) {
                return adapter.getItemId(position);
            }


            @Override
            public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
                adapter.onDetachedFromRecyclerView(recyclerView);
            }

            @Override
            public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
                super.registerAdapterDataObserver(observer);
                adapter.registerAdapterDataObserver(observer);
            }

            @Override
            public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
                super.unregisterAdapterDataObserver(observer);
                adapter.unregisterAdapterDataObserver(observer);
            }

            @Override
            public void setHasStableIds(boolean hasStableIds) {
                super.setHasStableIds(hasStableIds);
                adapter.setHasStableIds(hasStableIds);
            }

            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
                super.onViewRecycled(holder);
                adapter.onViewRecycled(holder);
            }

            @Override
            public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
                return adapter.onFailedToRecycleView(holder);
            }

            @Override
            public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
                adapter.onViewAttachedToWindow(holder);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
                adapter.onViewDetachedFromWindow(holder);
            }

            @Override
            public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
                adapter.onAttachedToRecyclerView(recyclerView);
            }
        };
    }


    static class LoadMoreHolder extends RecyclerView.ViewHolder {
        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }

}