<template>
  <div class="main-content">
    <!-- 列表 -->
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" style="justify-content:flex-start;">
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待提交" value="待提交"/>
              <el-option label="待审核" value="待审核"/>
              <el-option label="已通过" value="已通过"/>
              <el-option label="已驳回" value="已驳回"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="success" icon="el-icon-search" @click="search">查询</el-button>
          </el-form-item>
        </el-row>
      </el-form>

      <div class="table-content">
        <el-table
          class="tables"
          :data="dataList"
          v-loading="dataListLoading"
          border
          stripe
          style="width: 100%;">
          <el-table-column type="index" label="#" width="50" align="center"/>
          <el-table-column prop="fangwuId" label="房屋ID" width="120" align="center"/>
          <el-table-column prop="huzhuId" label="户主ID" width="120" align="center"/>
          <el-table-column prop="status" label="状态" width="100" align="center"/>
          <el-table-column prop="zhengmingFiles" label="证明文件" min-width="200" show-overflow-tooltip/>
          <el-table-column prop="shenheBeizhu" label="审核备注" min-width="200" show-overflow-tooltip/>
          <el-table-column prop="createTime" label="创建时间" width="160" align="center"/>
          <el-table-column prop="updateTime" label="更新时间" width="160" align="center"/>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button
                v-if="isAuth('fangwuzhengming','查看')"
                size="mini"
                type="text"
                @click="viewDetail(scope.row)"
              >详情</el-button>
              <el-button
                v-if="isAuth('fangwuzhengming','审核') && scope.row.status !== '已通过'"
                size="mini"
                type="primary"
                @click="openAudit(scope.row)"
              >审核</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          class="pagination-content"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageIndex"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          :total="totalPage"
          background
          style="margin-top: 10px; text-align: right;">
        </el-pagination>
      </div>
    </div>

    <!-- 审核弹窗 -->
    <el-dialog
      title="证明资料审核"
      :visible.sync="auditVisible"
      width="480px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="状态">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="已通过">已通过</el-radio>
            <el-radio label="已驳回">已驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            type="textarea"
            :rows="3"
            v-model="auditForm.shenheBeizhu"
            placeholder="可填写通过说明或驳回原因"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditVisible=false">取 消</el-button>
        <el-button type="primary" @click="submitAudit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      searchForm: {
        status: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      showFlag: true,
      auditVisible: false,
      auditForm: {
        id: null,
        status: '已通过',
        shenheBeizhu: ''
      }
    }
  },
  created () {
    this.getDataList()
  },
  methods: {
    isAuth (table, btn) {
      return this.$store.getters.roles && this.$store.getters.isAuth(table, btn)
        ? this.$store.getters.isAuth(table, btn)
        : true
    },
    search () {
      this.pageIndex = 1
      this.getDataList()
    },
    getDataList () {
      this.dataListLoading = true
      const params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id'
      }
      if (this.searchForm.status) {
        params.status = this.searchForm.status
      }
      this.$http({
        url: 'fangwuzhengming/page',
        method: 'get',
        params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list || []
          this.totalPage = data.data.total || 0
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    sizeChangeHandle (val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    currentChangeHandle (val) {
      this.pageIndex = val
      this.getDataList()
    },
    viewDetail (row) {
      this.$alert(
        `<div style="text-align:left;">
           <p><b>房屋ID：</b>${row.fangwuId || ''}</p>
           <p><b>户主ID：</b>${row.huzhuId || ''}</p>
           <p><b>状态：</b>${row.status || ''}</p>
           <p><b>证明文件：</b>${row.zhengmingFiles || ''}</p>
           <p><b>审核备注：</b>${row.shenheBeizhu || ''}</p>
         </div>`,
        '证明详情',
        { dangerouslyUseHTMLString: true }
      )
    },
    openAudit (row) {
      this.auditForm.id = row.id
      this.auditForm.status = row.status === '已驳回' ? '已驳回' : '已通过'
      this.auditForm.shenheBeizhu = row.shenheBeizhu || ''
      this.auditVisible = true
    },
    submitAudit () {
      if (!this.auditForm.id) {
        this.$message.error('缺少任务ID')
        return
      }
      this.$http({
        url: 'fangwuzhengming/audit',
        method: 'post',
        data: {
          id: this.auditForm.id,
          status: this.auditForm.status,
          shenheBeizhu: this.auditForm.shenheBeizhu
        }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success('审核完成')
          this.auditVisible = false
          this.getDataList()
        } else {
          this.$message.error((data && data.msg) || '审核失败')
        }
      })
    }
  }
}
</script>

<style scoped>
.form-content {
  margin-bottom: 10px;
}
</style>

