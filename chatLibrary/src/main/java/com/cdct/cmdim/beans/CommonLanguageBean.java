package com.cdct.cmdim.beans;

import java.util.List;

/**
 * Created by KING on 2018/3/7.
 */

public class CommonLanguageBean {

    /**
     * msg : 查询成功
     * code : 200
     * data : {"pageSize":"20","list":[{"CYSBM":"10001","RowNumBer":1,"CCYYNR":"您好，我是趙子龍趙醫生，很高興爲您服務？","CBM":"LS180306011370001"},{"CYSBM":"10001","RowNumBer":2,"CCYYNR":"您好，我是趙子龍趙醫生，您有問題要諮詢嗎？","CBM":"LS180302052150005"},{"CYSBM":"10001","RowNumBer":3,"CCYYNR":"您好，我是趙子龍趙醫生，您想諮詢哪方面的問題？","CBM":"LS180302052150004"},{"CYSBM":"10001","RowNumBer":4,"CCYYNR":"您好，我是趙子龍趙醫生，您有問題要諮詢嗎？","CBM":"LS180302052150003"},{"CYSBM":"10001","RowNumBer":5,"CCYYNR":"您好，我是趙子龍趙醫生，您有什麼問題需要諮詢呢？","CBM":"LS180302052150002"}],"pageNum":"1"}
     */
    private String msg;
    private String code;
    private DataEntity data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public DataEntity getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CommonLanguageBean{" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                '}';
    }

    public class DataEntity {
        /**
         * pageSize : 20
         * list : [{"CYSBM":"10001","RowNumBer":1,"CCYYNR":"您好，我是趙子龍趙醫生，很高興爲您服務？","CBM":"LS180306011370001"},{"CYSBM":"10001","RowNumBer":2,"CCYYNR":"您好，我是趙子龍趙醫生，您有問題要諮詢嗎？","CBM":"LS180302052150005"},{"CYSBM":"10001","RowNumBer":3,"CCYYNR":"您好，我是趙子龍趙醫生，您想諮詢哪方面的問題？","CBM":"LS180302052150004"},{"CYSBM":"10001","RowNumBer":4,"CCYYNR":"您好，我是趙子龍趙醫生，您有問題要諮詢嗎？","CBM":"LS180302052150003"},{"CYSBM":"10001","RowNumBer":5,"CCYYNR":"您好，我是趙子龍趙醫生，您有什麼問題需要諮詢呢？","CBM":"LS180302052150002"}]
         * pageNum : 1
         */
        private String pageSize;
        private List<ListEntity> list;
        private String pageNum;

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public void setList(List<ListEntity> list) {
            this.list = list;
        }

        public void setPageNum(String pageNum) {
            this.pageNum = pageNum;
        }

        public String getPageSize() {
            return pageSize;
        }

        public List<ListEntity> getList() {
            return list;
        }

        public String getPageNum() {
            return pageNum;
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "pageSize='" + pageSize + '\'' +
                    ", list=" + list +
                    ", pageNum='" + pageNum + '\'' +
                    '}';
        }

        public class ListEntity {
            /**
             * CYSBM : 10001
             * RowNumBer : 1
             * CCYYNR : 您好，我是趙子龍趙醫生，很高興爲您服務？
             * CBM : LS180306011370001
             */
            private String CYSBM;
            private int RowNumBer;
            private String CCYYNR;
            private String CBM;

            public void setCYSBM(String CYSBM) {
                this.CYSBM = CYSBM;
            }

            public void setRowNumBer(int RowNumBer) {
                this.RowNumBer = RowNumBer;
            }

            public void setCCYYNR(String CCYYNR) {
                this.CCYYNR = CCYYNR;
            }

            public void setCBM(String CBM) {
                this.CBM = CBM;
            }

            public String getCYSBM() {
                return CYSBM;
            }

            public int getRowNumBer() {
                return RowNumBer;
            }

            public String getCCYYNR() {
                return CCYYNR;
            }

            public String getCBM() {
                return CBM;
            }

            @Override
            public String toString() {
                return "ListEntity{" +
                        "CYSBM='" + CYSBM + '\'' +
                        ", RowNumBer=" + RowNumBer +
                        ", CCYYNR='" + CCYYNR + '\'' +
                        ", CBM='" + CBM + '\'' +
                        '}';
            }
        }
    }
}
