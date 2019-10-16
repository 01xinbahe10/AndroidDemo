package hxb.xb_testandroidfunction.test_about_recyclerview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_about_recyclerview.ItemHelper.ItemTouchHelper;
import hxb.xb_testandroidfunction.test_about_recyclerview.adapter.RecyclerAdapter;

/**
 * Created by hxb on 2019/4/9.
 */
public class TestAboutRecyclerViewActivity extends FragmentActivity {
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerAdapter mAdapter;
    private View mllGeneralParent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_recyclerview);
        mRecyclerView = findViewById(R.id.recyclerView);
        mllGeneralParent = findViewById(R.id.llGeneralParent);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        mAdapter = new RecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        TimerUtil timerUtil = TimerUtil.init();
        timerUtil.setDuration(0)
                .setTimingOrder(TimerUtil.STATE.DECREMENT)
                .setFeedback(new TimerUtil.Feedback() {
                    @Override
                    public boolean onFeedback(long currentTime) {
                        Log.e("TAG", "onFeedback: " + currentTime);

                        return false;
                    }
                });
        timerUtil.start();

        /**-----------------------------------*/
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        RectF rectF = new RectF(0f, 0f, bitmap.getWidth(), bitmap.getHeight());//先画一个矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//创建画笔
        paint.setColor(Color.RED);//添加画笔颜色
        paint.setStyle(Paint.Style.FILL);
//            paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        PaintFlagsDrawFilter flagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
        canvas.setDrawFilter(flagsDrawFilter);
        int round = 20;
        canvas.drawRoundRect(rectF, round, round, paint);


        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        drawable.draw(canvas);
        mllGeneralParent.setBackground(drawable);


    }

    private ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {


        /**
         * 官方文档的说明如下：
         * o control which actions user can take on each view, you should override getMovementFlags(RecyclerView, ViewHolder)
         * and return appropriate set of direction flags. (LEFT, RIGHT, START, END, UP, DOWN).
         * 返回我们要监控的方向，上下左右，我们做的是上下拖动，要返回都是UP和DOWN
         * 关键坑爹的是下面方法返回值只有1个，也就是说只能监控一个方向。
         * 不过点入到源码里面有惊喜。源码标记方向如下：
         *  public static final int UP = 1     0001
         *  public static final int DOWN = 1 << 1; （位运算：值其实就是2）0010
         *  public static final int LEFT = 1 << 2   左 值是3
         *  public static final int RIGHT = 1 << 3  右 值是8
         */
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            //也就是说返回值是组合式的
            //makeMovementFlags (int dragFlags, int swipeFlags)，看下面的解释说明
            int swipFlag = 0;
            //如果也监控左右方向的话，swipFlag=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            int dragflag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //等价于：0001&0010;多点触控标记触屏手指的顺序和个数也是这样标记哦
            return makeMovementFlags(dragflag, swipFlag);


            /**
             * 备注：由getMovementFlags可以联想到setMovementFlags，不过文档么有这个方法，但是：
             * 有 makeMovementFlags (int dragFlags, int swipeFlags)
             * Convenience method to create movement flags.便捷方法创建moveMentFlag
             * For instance, if you want to let your items be drag & dropped vertically and swiped left to be dismissed,
             * you can call this method with: makeMovementFlags(UP | DOWN, LEFT);
             * 这个recyclerview的文档写的简直完美，示例代码都弄好了！！！
             * 如果你想让item上下拖动和左边滑动删除，应该这样用： makeMovementFlags(UP | DOWN, LEFT)
             */

            //拓展一下：如果只想上下的话：makeMovementFlags（UP | DOWN, 0）,标记方向的最小值1
        }


        /**
         * 官方文档的说明如下
         * If user drags an item, ItemTouchHelper will call onMove(recyclerView, dragged, target). Upon receiving this callback,
         * you should move the item from the old position (dragged.getAdapterPosition()) to new position (target.getAdapterPosition())
         * in your adapter and also call notifyItemMoved(int, int).
         * 拖动某个item的回调，在return前要更新item位置，调用notifyItemMoved（draggedPosition，targetPosition）
         * viewHolde:正在拖动item
         * target：要拖到的目标
         * @return true 表示消费事件
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            //直接按照文档来操作
            mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            //注意:对应的数据也要交换
//            Collections.swap(mAdapter.getList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                adapter.refresh();
            // TODO: 2019/1/15 这里需要一个连续的延迟事件，且只能执行一次

//            mHandler.removeMessages(BriefingPageContract.ADAPTER_REFRESH);
//            mHandler.sendEmptyMessageDelayed(BriefingPageContract.ADAPTER_REFRESH, 2000);
            return true;
        }

        /**
         * 谷歌官方文档说明如下：
         * 这个看了一下主要是做左右拖动的回调
         * When a View is swiped, ItemTouchHelper animates it until it goes out of bounds, then calls onSwiped(ViewHolder, int).
         * At this point, you should update your adapter (e.g. remove the item) and call related Adapter#notify event.
         * @param viewHolder
         * @param i
         */

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        }

        /**
         * 官方文档如下：返回true 当前tiem可以被拖动到目标位置后，直接”落“在target上，其他的上面的tiem跟着“落”，
         * 所以要重写这个方法，不然只是拖动的tiem在动，target tiem不动，静止的
         * Return true if the current ViewHolder can be dropped over the the target ViewHolder.
         * @param recyclerView
         * @param current
         * @param target
         * @return
         */
        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            return true;
        }


        /**
         * 官方文档说明如下：
         * Returns whether ItemTouchHelper should start a drag and drop operation if an item is long pressed.
         * 是否开启长按 拖动
         * @return
         */
        @Override
        public boolean isLongPressDragEnabled() {
            //return true后，可以实现长按拖动排序和拖动动画了
            return true;
        }

        /**
         *
         * 测试区域点击拖动(成功)
         * */
        @Override
        public boolean clickLocalDrag(float rawX, float rawY, RecyclerView.ViewHolder vh) {
            if (vh instanceof RecyclerAdapter.MyViewHolder) {
                RecyclerAdapter.MyViewHolder viewHolder = (RecyclerAdapter.MyViewHolder) vh;
//                ArrayList<View> TouchableViews = viewHolder.itemView.getTouchables();
                int[] position = new int[2];
                viewHolder.textView.getLocationInWindow(position);
                Log.e("ItemTouchHelper", "clickLocalDrag: ---------------<<<>>>  X: " + position[0] + "    Y:" + position[1]);
                Log.e("ItemTouchHelper", "clickLocalDrag: ---------------<<<>>>  width: " + viewHolder.textView.getWidth() + "    height:" + viewHolder.textView.getHeight());

                boolean isW = rawX >= position[0] && rawX <= position[0] + viewHolder.textView.getWidth();
                boolean isH = rawY >= position[1] && rawY <= position[1] + viewHolder.textView.getHeight();

                return !(isW && isH);

            }
            return true;
        }
    };
}
