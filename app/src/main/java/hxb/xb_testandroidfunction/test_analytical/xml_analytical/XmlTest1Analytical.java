package hxb.xb_testandroidfunction.test_analytical.xml_analytical;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxb on 2019/1/18
 * <p>
 * 目的：将blockly中生成的xml 解析成一个对象，一个block包含哪些子block和嵌套关系等；
 * 再进行判断block与block连接关系和包含嵌套关系是否正常等。
 */
public class XmlTest1Analytical {
    private final String TAG = "XmlTest1Analytical";

    private final String M_BLOCK = "block";
    private final String M_TRIGGERING_TIME = "time_triggering";//时间触发
    private final String M_TYPE = "type";
    private final String M_ID = "id";
    private final String M_FIELD = "field";
    private final String M_TIME_START_TIME = "start_time";
    private final String M_TIME_STOP_TIME = "stop_time";
    private final String M_NAME = "name";
    private final String M_VALUE = "value";
    private final String M_STATEMENT = "statement";
    private final String M_NEXT = "next";

    public void draggingVerifyXmlFormat(InputStream inputStream) {
        try {
//            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            //在android下使用xmlpullparser进行解析
            XmlPullParser xmlPullParser = Xml.newPullParser();
            //设置xmlpullparser的一些参数
            xmlPullParser.setInput(inputStream, "utf-8");
//            //获取pull解析器对应事件类型
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        onlyParseTimeTriggering(xmlPullParser);
                        break;
                    case XmlPullParser.END_TAG:
//                        loge("hxb测试：：：：   "+xmlPullParser.getName());
//                        loge("--------------------------------------");
                        break;
                }

                eventType = xmlPullParser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 判断并只解析time_triggering 该类型标签下的内容
     */
    private void onlyParseTimeTriggering(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        switch (xmlPullParser.getName()) {
            case M_BLOCK:
                int count = xmlPullParser.getAttributeCount();
                for (int i = 0; i < count; i++) {

                    String blockType = xmlPullParser.getAttributeValue(i);
                    if (blockType.equals(M_TRIGGERING_TIME)) {//只解析time_triggering中的东西
                        BlockBean blockBean = new BlockBean();
                        blockBean.setBlockType(blockType);
                        blockBean.setBlockID(xmlPullParser.getAttributeValue(null, M_ID));

                        onlyParsePortraitBlock(xmlPullParser, blockBean);

                    }
                }
                break;
        }

    }

    /**
     * 只解析 左边第一纵向 的Block
     */
    private BlockBean mBlockBean;//记录有子元素的block

    private void onlyParsePortraitBlock(XmlPullParser xmlPullParser, BlockBean blockStartIndex) throws IOException, XmlPullParserException {

        int event = 0;
        int depth = xmlPullParser.getDepth();

        //记录时间
        String timeTriggering = blockStartIndex.getBlockType();
        int timeTriggeringStartTime = 0, timeTriggeringStopTime = 0;

        BlockBean previousBlock = blockStartIndex;
        BlockBean currentBlock = null;


        while ((event = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT
                && (xmlPullParser.getDepth() > depth || event != XmlPullParser.END_TAG)) {

            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue;
            }
            switch (xmlPullParser.getName()) {
                case M_BLOCK:

                    currentBlock = new BlockBean();
                    currentBlock.setBlockType(xmlPullParser.getAttributeValue(null, M_TYPE));
                    currentBlock.setBlockID(xmlPullParser.getAttributeValue(null, M_ID));


//                    int resultCodeB = verifyBlockNeighborRelation(previousBlock, nextBlock, true);
//                    if (resultCodeB != BlocklyContract.ERR_OK) {
//                        return resultCodeB;
//                    }
                    Log.e(TAG, "onlyParsePortraitBlock:  上一个：" + previousBlock.getBlockType() + "  当前的：" + currentBlock.getBlockType());
                    timeTriggering = "";//变更type类型避免NEXT中会多次判断

                    break;
                case M_FIELD:
                    //获取时间
                    String time = xmlPullParser.getAttributeValue(null, M_NAME);
                    if (time.equals(M_TIME_START_TIME)) {
                        try {
                            timeTriggeringStartTime = parseTriggerTime(xmlPullParser.nextText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (time.equals(M_TIME_STOP_TIME)) {
                        try {
                            timeTriggeringStopTime = parseTriggerTime(xmlPullParser.nextText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case M_VALUE:
                    if (null != currentBlock) {
                        onlyParseBlockValue(xmlPullParser, currentBlock);
                    }

                    break;
                case M_STATEMENT:
                    if (null != currentBlock) {

                        mBlockBean = new BlockBean();
                        mBlockBean.setBlockID(currentBlock.getBlockID());
                        mBlockBean.setBlockType(currentBlock.getBlockType());
//
//                        recordFunNum = 0;

//                        int resultCodeS =
                        onlyParsePortraitBlockStatement(xmlPullParser, mBlockBean);
                        //这里要判断code 是因为有些子block有value字节点需要判断
//                        if (resultCodeS != BlocklyContract.ERR_OK) {
//                            return resultCodeS;
//                        }


                        BlockBean blockBean = mBlockBean;
//                        while (null != blockBean) {
//                            //这里判断是需要整个对象不能包含哪些元素或是重复元素判断
//
//                            blockStatistics(blockBean);
//
//                            blockBean = blockBean.blockBean;
//                        }

                        for (int i = 0; i <blockBean.blockList.size() ; i++) {
                            Log.e(TAG, "onlyParsePortraitBlock:   type:"+blockBean.blockList.get(i).getBlockType()+"   id:"+blockBean.blockList.get(i).getBlockID() +"      大小："+blockBean.blockList.get(i).blockList.size());
                        }
                        depthA = -1;
                        mBlockBean = null;
//                        mLastBlockBean = null;
//
                    }
                    break;
                case M_NEXT:

                    //保证是Block的类型是time_triggering才执行里面的判断
                    if (timeTriggering.equals(M_TRIGGERING_TIME)) {//首次会进入这里
                        previousBlock.setStartTime(timeTriggeringStartTime);
                        previousBlock.setEndTime(timeTriggeringStopTime);

                    }

                    if (null != currentBlock) {
                        previousBlock = currentBlock;
                    }

                    break;
            }
        }

    }

    /**
     * 只解析单个block 中的Value
     */
    private void onlyParseBlockValue(XmlPullParser xmlPullParser, BlockBean block) throws IOException, XmlPullParserException {
        int event = 0;
        int depth = xmlPullParser.getDepth();

        BlockBean nextBlock = null;

        while ((event = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT
                && (xmlPullParser.getDepth() > depth || event != XmlPullParser.END_TAG)) {//只解析当前的标签内部的内容

            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                continue;
            }

            if (event == XmlPullParser.START_TAG) {
                String node = xmlPullParser.getName();
                switch (node) {
                    case M_BLOCK:
                        nextBlock = new BlockBean();
                        nextBlock.setBlockType(xmlPullParser.getAttributeValue(null, M_TYPE));
                        nextBlock.setBlockID(xmlPullParser.getAttributeValue(null, M_ID));

                        block.setValueBlockType(nextBlock.getBlockType());

                        //由于该方法每次都执行一次，就直接return 结果
//                        return verifyBlockNeighborRelation(block, nextBlock, false);
                }
//                loge("---------------VALUE-----------------  : " + node + "              " + xmlPullParser.getAttributeValue(0) + "  隶属纵向Block：" + block.getBlockType());
            }

        }
    }

    /**
     * 只解析 单个block 中的Statement 且 左边第一纵向
     */
    //记录 mBlockBean 对象中最后一个BlockBean，或是指定的BlockBean对象
    private BlockBean mLastBlockBean;
    private int recordFunNum = 0;//记录父block下有多少层，最后用于定位mLastBlockBean

    private int depthA = -1;
    private void onlyParsePortraitBlockStatement(XmlPullParser xmlPullParser, BlockBean block) throws IOException, XmlPullParserException {
        int event = 0;
        int depth = xmlPullParser.getDepth();
        if (depthA == depth){
            depth+=1;
        }
//        Log.e(TAG, "onlyParsePortraitBlockStatement: nnnnnnnnnnnnnnnnnnnnnn     "+depth );

        BlockBean parentBlock = block;
        BlockBean currentBlock = null;
        BlockBean previousBlock = parentBlock;

//        mLastBlockBean = returnSpecifiedObject();
//        loge(" 12345678  "+mLastBlockBean.getBlockType());

        mLastBlockBean = returnSpecifiedObject();
        while ((event = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT
                && (xmlPullParser.getDepth() > depth || event != XmlPullParser.END_TAG)) {//只解析当前的标签内部的内容

            if (event == XmlPullParser.TEXT || event == XmlPullParser.END_TAG) {
                if (event == xmlPullParser.END_TAG) {//xml 结束节点
                    String node = xmlPullParser.getName();
                    switch (node) {
                        case M_STATEMENT:
                            Log.e(TAG, "onlyParsePortraitBlockStatement:      结束节点  " + node + "    depth:" + depth);
                            /*
                             * 这里需要变换BlockBean 对象层次，是因为该方法是一个递归模式；
                             *
                             * 当 START_TAG 中 标签为M_STATEMENT它时，此时mLastBlockBean
                             * 是一个新的子对象了并递归；
                             *
                             * 如不在 END_TAG 中 标签为M_STATEMENT它时，把当前子对象切换到该对象的父对象，
                             * 则 START_TAG 中的M_BLOCK 添加新的BlockBean 是用的子对象，这就造成层次
                             * 包含子元素混乱。
                             *
                             * 以下是简单的xml逻辑顺序:
                             *
                             * <xml>
                             *   <block>
                             *       <statement>
                             *           <block ..../>
                             *           <next>
                             *               <block ..../>
                             *               ......
                             *           </next>
                             *       </statement>
                             *       <next>
                             *           <block .../>
                             *           .......
                             *       </next>
                             *   </block>
                             *</xml>
                             *
                             * 由于可视化编程，串联的block的xml都是采用套娃模式，进行数据反应，所以在设计
                             * BlockBean对象时，也是采用套娃模式。
                             *
                             * */

//                            Log.e(TAG, " 现在层次位置： " + mLastBlockBean.getBlockType());
                            BlockBean blockBean = mBlockBean;
                            recordFunNum--;
                            if (recordFunNum < 0) {
                                recordFunNum = 0;
                            }
                            int is = 0;
                            while (null != blockBean) {
//                                Log.e(TAG, " sType----->  " + blockBean.getBlockType() );

                                if (is == recordFunNum) {
                                    mLastBlockBean = blockBean;
                                }
                                is++;
                                blockBean = blockBean.blockBean;

                            }
//                            Log.e(TAG, "变换之后层次： " + mLastBlockBean.getBlockType());

                            break;
                    }
                }
                continue;
            }


            if (event == XmlPullParser.START_TAG) {
                String node = xmlPullParser.getName();
                switch (node) {
                    case M_BLOCK:
//                        Log.e(TAG, "onlyParsePortraitBlockStatement: mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  "+depth );

                        currentBlock = new BlockBean();
                        currentBlock.setBlockType(xmlPullParser.getAttributeValue(null, M_TYPE));
                        currentBlock.setBlockID(xmlPullParser.getAttributeValue(null, M_ID));

//                        int resultCodeB = verifyBlockNeighborRelation(previousBlock, nextBlock, true);
//                        if (resultCodeB != BlocklyContract.ERR_OK) {
//                            return resultCodeB;
//                        }
//                        Log.e(TAG, "onlyParsePortraitBlockStatement:  父类block:"+mLastBlockBean.getBlockType()+"  id:"+mLastBlockBean.getBlockID()+"  当前的："+currentBlock.getBlockType()+" 当前id:"+currentBlock.getBlockID() );
//                        mLastBlockBean.blockList.add(nextBlock);
                        mLastBlockBean.blockList.add(currentBlock);
                        break;
                    case M_VALUE:
                        if (null != currentBlock) {

//                            int resultCodeV =
                            onlyParseBlockValue(xmlPullParser, currentBlock);
//                            if (resultCodeV != BlocklyContract.ERR_OK) {
//                                return resultCodeV;
//                            }
                        }

                        break;
                    case M_STATEMENT:
                        if (null != currentBlock) {

                            mLastBlockBean.blockBean = currentBlock;
                            ++recordFunNum;//层次记录
//                            int resultCodeS =
                            depthA = xmlPullParser.getDepth();
//                            Log.e(TAG, "onlyParsePortraitBlockStatement: ------------------STATEMENT-----------------3333333333   " + depthA);
                            onlyParsePortraitBlockStatement(xmlPullParser, mLastBlockBean.blockBean);
//                            if (resultCodeS != BlocklyContract.ERR_OK) {
//                                return resultCodeS;
//                            }

                        }

                        break;
                    case M_NEXT:
                        if (null != currentBlock) {
                            previousBlock = currentBlock;
//                            loge("------------------STATEMENT----------------44444444444   :"+node+"           "+nextBlock.getBlockType()+"隶属纵向Block:"+block.getBlockType());
                        }
                        break;
                }
            }

        }

    }

    /**
     * 该方法判断是否是BlockBean 中的BlockBean对象是否是最后一个
     */
    private BlockBean returnSpecifiedObject() {
        String lastBlockId = "";//用于判断是否是最后一个BlockBean
        BlockBean mLast1BlockBean = mBlockBean.blockBean;
        if (null == mLast1BlockBean) {
            return mBlockBean;
        }
        while (null != mLast1BlockBean) {//定位到最后一个class
            if (!TextUtils.isEmpty(mLast1BlockBean.getBlockID())) {
                lastBlockId = mLast1BlockBean.getBlockID();
            }
            mLast1BlockBean = mLast1BlockBean.blockBean;
        }

        BlockBean mLast2BlockBean = mBlockBean.blockBean;
        while (null != mLast2BlockBean) {
            if (TextUtils.equals(mLast2BlockBean.getBlockID(), lastBlockId)) {
                break;
            }
            mLast2BlockBean = mLast2BlockBean.blockBean;
        }

        return mLast2BlockBean;

    }

    /**
     * 遍历BlockBean 中存的子block，统计并验证
     */
    private void blockStatistics(BlockBean blockBean) {
//        Log.e(TAG, "blockStatistics: 父类：" + blockBean.getBlockType() + " id:" + blockBean.getBlockID() + "    子大小：" + blockBean.blockList.size() );
        if (blockBean.blockList.size() > 0) {
            for (BlockBean b : blockBean.blockList) {
                Log.e(TAG, "blockStatistics:   父类：" + blockBean.getBlockType() + " id:" + blockBean.getBlockID() + "    子：" + b.getBlockType());
                if (blockBean.blockList.size() > 0) {
                    blockStatistics(b);

                }
            }
        } else {
//            Log.e(TAG, "blockStatistics:   父类：" + blockBean.getBlockType() + " id:" + blockBean.getBlockID() + "    子： 没有");
        }
    }

    /**
     * 解析时间
     *
     * @param time
     * @return
     */
    public int parseTriggerTime(String time) throws Exception {
        String h = time.substring(0, 2);
        int hour = Integer.valueOf(h);
        String m = time.substring(3, 5);
        int min = Integer.valueOf(m);
        return hour * 100 + min;
    }

    /**
     * 该类主要做不同的block的状态阐述
     */
    public static class BlockBean {
        private String blockType = "";//block的type
        private String blockID = "";//block的id
        private String nextBlockType = "";//表示该block的下一个连接的type
        private String valueBlockType = "";//表示该block有valueBlock的type
        private int startTime;
        private int endTime;

        public BlockBean blockBean;//只记录层次block并且该block有下一层才记录，每一层只记录一个
        public List<BlockBean> blockList = new ArrayList<>();//记录该block下有多少个子block

        public String getValueBlockType() {
            return valueBlockType;
        }

        public void setValueBlockType(String valueBlockType) {
            this.valueBlockType = valueBlockType;
        }

        public String getBlockType() {
            return blockType;
        }

        public String getBlockID() {
            return blockID;
        }


        public void setBlockType(String blockType) {
            this.blockType = blockType;
        }

        public void setBlockID(String blockID) {
            this.blockID = blockID;
        }


        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public String getNextBlockType() {
            return nextBlockType;
        }

        public void setNextBlockType(String nextBlockType) {

            this.nextBlockType = nextBlockType;
        }

    }
}
