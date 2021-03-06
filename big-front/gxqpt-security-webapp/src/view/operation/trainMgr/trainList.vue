<template>
	<Layout>
		<Content>
			<Breadcrumb>
				<BreadcrumbItem>培训管理</BreadcrumbItem>
				<BreadcrumbItem>培训列表</BreadcrumbItem>
			</Breadcrumb>
			<Card>
				<Form :model="searchCondition" ref="searchformData" inline :label-width="80">
                    <FormItem label="培训时间" prop="serviceDate">
						<DatePicker type="daterange" v-model="trainTime" :editable="false" placeholder="请选择服务时间" style="width: 200px"></DatePicker>
					</FormItem>
                    <FormItem label="培训类别">
                      <Select v-model="currentTypeIdx" style="width:160px">
                        <Option :value="0">全部</Option>
                        <Option :value="i + 1" v-for="(item, i) in typeList" :key="i">{{item.type}}</Option>
                      </Select>
                    </FormItem>
                    <FormItem label="培训名称">
                        <Input type="text" v-model="searchCondition.name"></Input>
                    </FormItem>
                    <!--<FormItem label="培训内容" prop="context">
                        <Input v-model="searchCondition.context"></Input>
					</FormItem>-->
					<FormItem label="状态" prop="status" style="width: 177px;">
						<Select v-model="searchCondition.status">
                            <Option value="0">全部</Option>
							<Option :value="1">申请</Option>
							<Option :value="2">驳回</Option>
							<Option :value="3">同意</Option>
                            <Option :value="4">驳回后申请</Option>
							<Option :value="5">完结</Option>
						</Select>
					</FormItem>
                    <FormItem label="任务编号" style="width: 200px">
                        <Input v-model="searchCondition.applyCode"></Input>
                    </FormItem>
					<FormItem :label-width="20">
						<Button type="primary" @click="preSearch" v-if="authButton.includes('ops_train_list_query')">查询</Button>
					</FormItem>
				</Form>
                <hy-table
                    highlight-row
                    border
                    :current="pageInfo.pageNo"
                    :columns="tableList.columns"
                    :data="tableList.data"
                    :total="Number(pageInfo.total)"
                    :pageSize="Number(pageInfo.pageSize)"
                    pageType="small"
                    show-elevator
                    show-sizer
                    ref="safetyEvalTable"
                    @on-page-change="pageChange" />
			</Card>
		</Content>
        <trainDetailModal
            :detailData="currentActTrainInfo"
            ref="trainDetailModal"
            title="培训修改"
            :isEdit="true"
            @on-ok="updateApply"
            :typeList="typeList">
        </trainDetailModal>
        <pointModal @on-ok="doRate" ref="pointModal"></pointModal>
	</Layout>
</template>
<script>
import {mapState} from 'vuex'
import api from '@/api/axiosApi'
import operationApiList from '@/api/operationApiList'

import trainDetailModal from "./trainModal/trainDetailModal.vue";
import pointModal from "./trainModal/pointModal.vue";

function convertTime(time) {
    const date = new Date(time)
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`
}

function getDateRange(time) {
  if (!time) {
    return ''
  }
  // 结束日期
  const endDate = new Date(time)
  // 当前日期往前推30天
  const startDate = new Date(time - 30 * 24 * 60 * 60 * 1000)
  return {
    start: `${startDate.getFullYear()}-${startDate.getMonth() + 1}-${startDate.getDate()}`,
    end: `${endDate.getFullYear()}-${endDate.getMonth() + 1}-${endDate.getDate()}`
  }
}

export default {
    components: {
        trainDetailModal,
        pointModal
    },
    data() {
        const vm = this
        return {
            // 类型列表
            typeList: [],
            currentTypeIdx: 0,
            trainDetail: false,
            point: false,
            searchCondition: {
                context: '',
                name: '',
                status: '0',
                type: '',
                applyCode: this.$route.query.code || ''
            },
            trainTime: [],
            tableList: {
                columns: [{
                    title: '序号',
                    type: 'index',
                    width: 80,
                    align: 'center'
                }, {
                    title: '任务编号',
                    key: 'applyCode'
                }, {
                    title: '培训名称',
                    key: 'name'
                }, {
                    title: '培训类别',
                    key: 'type'
                }, {
                    title: '培训时间',
                    key: 'trainTime'
                }, {
                    title: '状态',
                    key: 'status',
                    render: (h, params) => {
                        const status = ['--', '申请', '驳回', '同意', '驳回后申请', '完结']
                        return h('span', status[params.row.status])
                    }
                }, {
                    title: '培训评分',
                    key: 'trainSorce'
                }, {
                    title: '培训反馈文件',
                    key: 'fileUrl',
                    align: 'center',
                    render: (h, params) => {
                        const {id, files, status, personId} = params.row
                        const {userId, token} = vm.$store.state.userInfo
                        const file = []
                        if (files) {
                            files.map(item => {
                                file.push(item.fileId)
                            })
                        }
                        if (file && file.length > 0 && status == 5 && vm.authButton.includes('ops_train_list_download')) {
                            return h('a', {
                                attrs: {
                                    href: `/api/file/p/file/downloadpackage?ids[]=${file.join(',')}&userId=${userId}&token=${token}`,
                                    target: '_blank'
                                }
                            }, [
                                h('Button', {
                                    props: {
                                        type: 'primary',
                                        size: 'small',
                                        icon:'ios-cloud-download-outline'
                                    },
                                    style: {
                                        marginRight: '5px'
                                    }
                                }, '下载')
                            ])
                        } else {
                            return h('span', '--')
                        }
                    }
                }, {
                    title: '操作',
                    type: 'act',
                    width: 200,
                    align: 'center',
                    render: (h, params) => {
                        const {status, trainSorce} = params.row
                        const detail = h('Button', {
                            props: {
                                type: 'primary',
                                size: 'small'
                            },
                            style: {
                                marginRight: '5px'
                            },
                            on: {
                                click: () => {
                                    this.$router.push({
                                        name: 'trainDetail',
                                        params: {
                                            id: params.row.id
                                        },
                                        query: {
                                            status: status
                                        }
                                    })
                                }
                            }
                        }, '详情')
                        const rate = h('Button', {
                            props: {
                                type: 'primary',
                                size: 'small'
                            },
                            style: {
                                marginRight: '5px',
                            },
                            on: {
                                click: () => {
                                    vm.currentActTrainInfo = params.row
                                    vm.$refs.pointModal.open()
                                }
                            }
                        }, '评价')
                        const edit = h('Button', {
                            props: {
                                type: 'primary',
                                size: 'small'
                            },
                            style: {
                                marginRight: '5px'
                            },
                            on: {
                                click: () => {
                                    vm.currentActTrainInfo = params.row
                                    vm.$refs.trainDetailModal.open(false, true, vm.currentActTrainInfo)
                                }
                            }
                        }, '修改')
                        const btns = []
                        // 驳回
                        if (vm.authButton.includes('ops_train_list_update') && status === 2) {
                            btns.push(edit)
                        }
                        // 已完结
                        if (vm.authButton.includes('ops_train_list_evaluate') && status === 5 && (!trainSorce && trainSorce != 0)) {
                            btns.push(rate)
                        }
                        if (vm.authButton.includes('ops_train_list_detail')) {
                            btns.push(detail)
                        }
                        return h('div', btns)
                    }
                }],
                data: []
            },
            pageInfo: {
                pageNo: 1,
                pageSize: 10,
                total: 0
            },
            // 当前修改的培训的信息
            currentActTrainInfo: {}
        }
    },
    computed: {
        ...mapState(['authButton'])
    },
    mounted () {
        this.doSearch()
        this.getTrainTypeList()
    },
    methods: {
        preSearch() {
            this.pageInfo.pageNo = 1
            this.doSearch()
        },
        pageChange(pageNo, pageSize) {
            this.pageInfo.pageNo = pageNo
            this.pageInfo.pageSize = pageSize
            this.doSearch()
        },
        // 查询所有的类型和名称的关系列表
        getTrainTypeList() {
            const vm = this
            api(operationApiList.trainTypeList)
            .then(res => {
                if (res.data.errcode === 0) {
                    const typeList = res.data.data
                    typeList.push({
                        type: '自定义培训'
                    })
                    vm.typeList = res.data.data
                }
            }, error => {console.log(error)})
        },
        // 查询
        doSearch () {
            this.getTrainList()
        },
        // 获取培训列表
        getTrainList(pageNo, pageSize) {
            const vm = this
            const endTime = vm.trainTime.length > 0 ? getDateRange(vm.trainTime[1]).end : ''
            const startTime = vm.trainTime.length > 0 ? getDateRange(vm.trainTime[0]).end : ''
            api(operationApiList.trainList, {
                pageNo: pageNo || vm.pageInfo.pageNo,
                pageSize: pageSize || vm.pageInfo.pageSize,
                data: {
                    ...vm.searchCondition,
                    endTime: endTime ? `${endTime} 23:59:59` : '',
                    startTime: startTime ? `${startTime} 00:00:00` : ''
                }
            }).then(res => {
                if (res.data.errcode === 0) {
                    const result = res.data.data
                    vm.pageInfo.pageNo = result.pageNum
                    vm.pageInfo.total = result.total
                    vm.tableList.data = result.list || []
                }
            }, error => {console.log(error)})
        },
        closeTrainDetail() {//关闭处理详情modal
            console.log("关闭1")
            this.trainDetail = false
        },
        closePointModal(){//关闭评分modal
            console.log("关闭3")
            this.point = false
        },
        // 更新培训
        updateApply(data) {
            const vm = this
            data.id = vm.currentActTrainInfo.id
            data.trainTime = convertTime(data.trainTime)
            api(operationApiList.trainUpdate, data)
            .then(res => {
                if (res.data.errcode === 0) {
                    vm.$Message.success('申请成功，等待审批！！')
                    vm.$refs.trainDetailModal.closeModal()
                    vm.getTrainList()
                }
            }, error => {console.log(error)})
        },
        // 评分
        doRate(data) {
            const vm = this
            api(operationApiList.trainEvaluate, {
                id: vm.currentActTrainInfo.id,
                score: data.score,
                advice: data.advice
            }).then(res => {
                if (res.data.errcode === 0) {
                    vm.$Message.success('评价成功！！！')
                    vm.doSearch()
                }
            }, error => {console.log(error)})
        }
    },
    watch: {
        currentTypeIdx () {
            if (this.currentTypeIdx === 0) {
                this.searchCondition.type = ''
            } else {
                this.searchCondition.type = this.typeList[this.currentTypeIdx - 1].type
            }
        }
    }
}
</script>
<style lang="less" scoped="scoped">
	.act-btn-group{
		button{
			margin: 0 12px;
		}
    }
    .ivu-modal-footer{
        display: none;
    }
</style>