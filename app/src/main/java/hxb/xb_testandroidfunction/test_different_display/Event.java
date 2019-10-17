package hxb.xb_testandroidfunction.test_different_display;

import hxb.xb_testandroidfunction.test_different_display.util.ControlBoardUtil;

public interface Event {
    interface ItemEvent {
        int OK = ControlBoardUtil.OK;

        int UP = ControlBoardUtil.UP;

        int DOWN = ControlBoardUtil.DOWN;

        int LEFT = ControlBoardUtil.LEFT;

        int RIGHT = ControlBoardUtil.RIGHT;

        int EXIT = ControlBoardUtil.EXIT;

        void onItemClick(int action);
    }
}
