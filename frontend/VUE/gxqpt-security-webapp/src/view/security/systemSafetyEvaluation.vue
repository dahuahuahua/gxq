<template>
	<Layout>
		<Content>
			<Breadcrumb>
				<BreadcrumbItem>安全保障平台</BreadcrumbItem>
				<BreadcrumbItem>系统安全评测</BreadcrumbItem>
			</Breadcrumb>
			<Card>
				<Form :model="searchformData" ref="searchformData" inline :label-width="60">
					<FormItem label="系统名称">
						<Select v-model="searchformData.applicationName" style="width: 150px;">
							<Option value="-1">全部</Option>
							<Option v-for="(item,index) in visibleForSelf" :value="item.name" :key="index">{{item.name}}</Option>
						</Select>
					</FormItem>
					<!-- <FormItem label="安全等级">
						<Select v-model="searchformData.level">
							<Option value="-1">全部</Option>
							<Option value="2">高</Option>
							<Option value="1">中</Option>
							<Option value="0">低</Option>
						</Select>
					</FormItem> -->
					<FormItem :label-width="30">
						<Button type="primary" @click="search" v-if="checkButton('system_safety_evaluation_search')">查询</Button>
					</FormItem>
				</Form>
				<hy-table border ref="safetyEvalTable" :data="safetyEvalData" :columns="safetyEvalCol" :current="pageOption.current" :total="pageOption.total" :page-size="pageOption.pageSize" @on-change="pageChange" @on-page-size-change="changePageSize" show-sizer show-elevator/>
			</Card>
		</Content>
		<Modal @on-visible-change="modalChange" on-cancel="closeModal" v-model="evalModal" title="评测系统" width="80%" :mask-closable="false">
			<div style="text-align:center">
				<Form ref="evalData" :model="evalData" :label-width="70" class="clearfix">
					<Row :gutter="40">
						<Col span="11" offset="1">
						<FormItem label="系统建设单位" :label-width="100">
							<Input v-model="evalData.company" disabled></Input>
						</FormItem>
						</Col>
						<Col span="11">
						<FormItem label="系统名称">
							<Input v-model="evalData.applicationName" disabled></Input>
						</FormItem>
						</Col>
						<Col span="22" offset="1">
						<FormItem label="系统承建商" :label-width="100">
							<Input v-model="evalData.contractor" disabled></Input>
						</FormItem>
						</Col>
						<Col span="22" offset="1">
						<Table height="250" :columns="modalType=='view'?SafetyLevalViewCol:SafetyLevalCol" :data="assessmentReportList" border> </Table>
						</Col>
						<Col span="22" offset="1" style="margin: 30px 0;">
						<Button size="large" type="ghost" class="left" v-show="modalType=='eval'" @click="isEvalForm=!isEvalForm">新增评测</Button>
						</Col>
					</Row>
				</Form>
				<Form ref="evalFormData" :model="evalFormData" :rules="ruleValidate" :label-width="100" class="clearfix" v-show="isEvalForm && modalType=='eval'">
					<Row :gutter="40">
						<Col span="12">
						<FormItem label="评测日期" prop="assessmentTime">
							<DatePicker type="date" :value="evalFormData.assessmentTime" :options="assessmentOptions" @on-change="assessmentTimeChange" placeholder="请选择日期" style="width:100%"></DatePicker>
						</FormItem>
						</Col>
						<Col span="12">
						<FormItem label="评测等级" prop="level">
							<Select v-model="evalFormData.level">
								<Option value="2">高</Option>
								<Option value="1">中</Option>
								<Option value="0">低</Option>
							</Select>
						</FormItem>
						</Col>
						<Col span="12">
						<FormItem label="评测报告" prop="evalReport">
							<hy-upload ref="evalReport" :format="['xls', 'xlsx']" :on-success="setEvalReport" :on-remove="removeEvalReport" :before-upload="evalBeforeUpload"></hy-upload>
						</FormItem>
						</Col>
						<Col span="12">
						<FormItem label="整改报告" prop="reformReport">
							<hy-upload ref="reformReport" :format="['xls', 'xlsx']" :on-success="setReformReport" :on-remove="removeReformReport" :before-upload="ReformBeforeUpload"></hy-upload>
							<div style="float:right;margin-right:20px;">
								<a @click="downloadFile(tempUrl,tempName)">整改模板下载</a>
							</div>
						</FormItem>
						</Col>
						<Col span="24">
						<FormItem label="备注" prop="remarks">
							<Input type="textarea" v-model="evalFormData.remarks" :maxlength="150" :rows="3"></Input>
						</FormItem>
						</Col>
						<Col span="24">
						<FormItem class="act-btn-group">
							<Button type="primary" @click="handleSubmit" size="large">增加</Button>
							<Button type="default" @click="closeModal" size="large">取消</Button>
						</FormItem>
						</Col>
					</Row>
				</Form>
			</div>
		</Modal>
	</Layout>
</template>
<script>
	import api from '@/api/axiosApi'
	import apiList from '@/api/securityApiList'
	import hyTable from '@/components/hengyun/table'
	import hyUpload from '@/components/hengyun/hyUpload'
	import { mapState } from 'vuex'
	export default {
		data() {
			return {
				roots: "",
				isEvalForm: false, //是否显示新增评测表单
				modalType: 'eval', //莫态框类型，点击查看弹出：view,点击评测弹出eval,默认是eval
				systemNames: [], //系统名称列表
				pageOption: { //分页配置参数
					current: 1,
					total: 0,
					pageSize: 10
				},
				evalModal: false,
				searchformData: {
					applicationName: '-1',
					level: '-1'
				},
				evalData: {},
				assessmentReportList:[],
				evalFormData: {
					assessmentTime: '',
					level: '2'
				},
				assessmentOptions: {
                    disabledDate (date) {
                        return date && date.valueOf() > Date.now();
                    }
                },
				safetyEvalCol: [{
					title: '序号',
					type: 'index',
					width: 60,
					align: 'center'
				}, {
					title: '系统建设单位',
					key: 'company'
				}, {
					title: '系统名称',
					key: 'applicationName'
				}, {
					title: '系统承建商',
					key: 'contractor'
				}, {
					title: '最近评测安全等级',
					key: 'level',
					render(h, params) {
						if(params.row.level == 0) {
							return h('div', [
								h('span', {
									style: {
										color: '#FD2A2A'
									}
								}, '低')
							])
						} else if(params.row.level == 1) {
							return h('div', [
								h('span', {
									style: {
										color: '#F7A55D'
									}
								}, '中')
							])
						} else if(params.row.level == 2) {
							return h('div', [
								h('span', {
									style: {
										color: '#70D493'
									}
								}, '高')
							])
						} else {
							return h('div', [
								h('span', {})
							])
						}
					}
				}, {
					title: '最近评测时间',
					key: 'assessmentTime'
				}, {
					title: '评测报告',
					key: 'assessmentFileName'
				}, {
					title: '整改报告',
					key: 'rectificationFileName'
				}, {
					title: '操作',
					type: 'act',
					width: 150,
					align: 'center',
					render: (h, params) => {
						return h('div', [
							h('Button', {
								props: {
									type: 'primary',
									size: 'small'
								},
								style: {
									marginRight: '5px',
									display: this.checkButton('system_safety_evaluation_assessment') ? 'inline-block' : 'none'
								},
								on: {
									click: () => {
										this.modalType = 'eval';
										this.evalModal = true;
										this.evalFormData.assessmentId = params.row.id;
										this.viewSafetyEvalusion(params.row.id,params.row);
									}
								}
							}, '评测'),
							h('Button', {
								props: {
									type: 'primary',
									size: 'small'
								},
								style: {
									display: this.checkButton('system_safety_evaluation_view') ? 'inline-block' : 'none'
								},
								on: {
									click: () => {
										this.modalType = 'view';
										this.evalModal = true;
										this.viewSafetyEvalusion(params.row.id,params.row);
									}
								}
							}, '查看')
						]);
					}
				}],
				tempUrl:'http://127.0.0.1:6080/group1/M00/00/04/wKgemlt1PraAWML2AABmAGhf-Po284.xls',
				tempName:'系统安全漏洞整改报告.xls',
				safetyEvalData: [],
				SafetyLevalCol: [{ //点击评测时的表头
						type: 'index',
						width: 60,
						align: 'center'
					},
					{
						title: '评测安全等级',
						key: 'level',
						render(h, params) {
							if(params.row.level == 0) {
								return h('div', [
									h('span', {
										style: {
											color: '#FD2A2A'
										}
									}, '低')
								])
							} else if(params.row.level == 1) {
								return h('div', [
									h('span', {
										style: {
											color: '#F7A55D'
										}
									}, '中')
								])
							} else {
								return h('div', [
									h('span', {
										style: {
											color: '#70D493'
										}
									}, '高')
								])
							}
						}
					},
					{
						title: '评测时间',
						key: 'assessmentTime',
						render(h, params) {
							var _this = this;
							if(params.row.assessmentTime) {
								let time = params.row.assessmentTime.substr(0,10);
								return h('div', [
									h('span', time),
								])
							} else {
								return h('div', [
									h('span', '—'),
								])
							}
						}
					},
					{
						title: '评测报告',
						key: 'assessmentFileName',
						render(h, params) {
							var _this = this;
							if(params.row.assessmentFileName) {
								let fileUrl = params.row.assessmentFileUrl;
								let fileName = params.row.assessmentFileName;
								let urlDownload = "/api/file/file/download?url=" + fileUrl + "&filename=" + fileName;
								return (<a href={urlDownload} target="_black">{params.row.assessmentFileName}</a>);
							} else {
								return h('div', [
									h('span', '无'),
								])
							}
						}
					},
					{
						title: '整改报告',
						key: 'rectificationFileName',
						render(h, params) {
							var _this = this;
							if(params.row.rectificationFileName) {
								let fileUrl = params.row.rectificationFileUrl;
								let fileName = params.row.rectificationFileName;
								let urlDownload = "/api/file/file/download?url=" + fileUrl + "&filename=" + fileName;
								return (<a href={urlDownload} target="_black">{params.row.rectificationFileName}</a>);
							} else {
								return h('div', [
									h('span', '无'),
								])
							}
						}
					},
					{
						title: '备注',
						key: 'remarks',
					},
					{
						title: '操作',
						type: 'act',
						width: 80,
						align: 'center',
						render: (h, params) => {
							let _this = this;
							return h('div', [
								h('Button', {
									props: {
										type: 'error',
										size: 'small'
									},
									style: {
										marginRight: '5px'
									},
									on: {
										click() {
											_this.deleteAssessmentReport(params.row.id);
										}
									}
								}, '删除'),
							])
						}
					}
				],
				SafetyLevalViewCol: [{ //点击查看时的表头
						type: 'index',
						width: 60,
						align: 'center'
					},
					{
						title: '评测安全等级',
						key: 'level',
						render(h, params) {
							if(params.row.level == 0) {
								return h('div', [
									h('span', {
										style: {
											color: '#FD2A2A'
										}
									}, '低')
								])
							} else if(params.row.level == 1) {
								return h('div', [
									h('span', {
										style: {
											color: '#F7A55D'
										}
									}, '中')
								])
							} else {
								return h('div', [
									h('span', {
										style: {
											color: '#70D493'
										}
									}, '高')
								])
							}
						}
					},
					{
						title: '评测时间',
						key: 'assessmentTime',
						render(h, params) {
							var _this = this;
							if(params.row.assessmentTime) {
								let time = params.row.assessmentTime.substr(0,10);
								return h('div', [
									h('span', time),
								])
							} else {
								return h('div', [
									h('span', '—'),
								])
							}
						}
					},
					{
						title: '评测报告',
						key: 'assessmentFileName',
						render(h, params) {
							var _this = this;
							if(params.row.assessmentFileName) {
								let fileUrl = params.row.assessmentFileUrl;
								let fileName = params.row.assessmentFileName;
								let urlDownload = "/api/file/file/download?url=" + fileUrl + "&filename=" + fileName;
								return (<a href={urlDownload} target="_black">{params.row.assessmentFileName}</a>);
							} else {
								return h('div', [
									h('span', '无'),
								])
							}
						}
					},
					{
						title: '整改报告',
						key: 'rectificationFileName',
						render(h, params) {
							var _this = this;
							if(params.row.rectificationFileName) {
								let fileUrl = params.row.rectificationFileUrl;
								let fileName = params.row.rectificationFileName;
								let urlDownload = "/api/file/file/download?url=" + fileUrl + "&filename=" + fileName;
								return (<a href={urlDownload} target="_black">{params.row.rectificationFileName}</a>);
							} else {
								return h('div', [
									h('span', '无'),
								])
							}
						}
					},
					{
						title: '备注',
						key: 'remarks'
					}
				],
				ruleValidate: {
					assessmentTime: [{
						required: true,
						message: '评测日期不可以为空',
						trigger: 'blur'
					}],
					level: [{
						required: true,
						message: '评测等级不可以为空',
						trigger: 'blur'
					}],
					evalReport: [{
						required: true,
						message: '评测报告不可以为空',
						trigger: 'blur'
					}],
					reformReport: [{
						required: true,
						message: '整改报告不可以为空',
						trigger: 'blur'
					}],
					remarks: [{
						required: true,
						message: '备注不可以为空',
						trigger: 'blur'
					}]
				}
			}
		},
		computed: {
			...mapState([
				'authButton',
				'visibleForSelf'
			])
		},
		components: {
			hyTable,
			hyUpload
		},
		methods: {
			init() {
				this.getSystemName();
				var d = new Date();
				this.evalFormData.assessmentTime = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate();
			},
			getSystemName() {
				api(apiList.getSystemName,{type:1}).then((res) => {
					if(res.status == 200 && res.data.data) {
						console.log(res);
						this.systemNames = res.data.data;
						this.getSafetyEvalusion();
					}
				}, (err) => {
					//dong something...
				})
      },
      search() {
				this.pageOption.current=1;
				this.getSafetyEvalusion();
      },
			getSafetyEvalusion() {
				let params = {
					data: this.searchformData,
					pageNo: this.pageOption.current,
					pageSize: this.pageOption.pageSize
				}
				api(apiList.getAssessment, params).then((res) => {
					if(res.data.status = 200) {
						this.safetyEvalData = res.data.data.list;
						this.pageOption.total = res.data.data.total;
					}
				}, (err) => {
					//do something...
				})
			},
			pageChange(num) { //页码改变的回调
				this.pageOption.current = num;
				this.getSafetyEvalusion();
			},
			changePageSize(num) { //切换每页条数时的回调
				this.pageOption.pageSize = num;
				this.getSafetyEvalusion();
			},
			viewSafetyEvalusion(id,params) {//评测列表
				api(apiList.viewAssessment, {
					id: id
				}).then((res) => {
					if(res.status == 200) {
						if(!!params){
							this.evalData = {
								company:params.company,
								applicationName:params.applicationName,
								contractor:params.contractor,
							};
						}
						this.evalData.id = id;
						this.assessmentReportList = res.data.data;
						// this.$set(this.evalData.assessmentReportList);
					}
				}, (err) => {

				})
			},
			modalChange(status) { //莫态框状态改变事件
				if(status) {
					document.querySelector('.ivu-modal-footer').style.display = 'none';
				} else {
					this.isEvalForm = false;
					this.$refs.evalFormData.resetFields();
					this.$refs.evalReport.$children[0].clearFiles();
					this.$refs.reformReport.$children[0].clearFiles();
				}
			},
			closeModal() {
			  this.$refs.evalFormData.resetFields();
				this.evalModal = false;
				this.getSafetyEvalusion();
			},
			evalBeforeUpload() { //评测报告单文件上传
				this.$refs.evalReport.$children[0].clearFiles();
			},
			ReformBeforeUpload() { //整改报告单文件上传
				this.$refs.reformReport.$children[0].clearFiles();
			},
			formatErrorFun (file, fileList) {
        this.$Message.error('文件类型错误，请重新上传!');
      },
			save() {
				//do something
				api(apiList.createAssessmentReport, this.evalFormData).then((res) => {
					if(res.data.errmsg=="ok"){
						this.$refs.evalFormData.resetFields();
						this.$refs.evalReport.$children[0].clearFiles();
						this.$refs.reformReport.$children[0].clearFiles();
						this.$Message.info("新增成功！");
						this.viewSafetyEvalusion(this.evalFormData.assessmentId);
					}else{
						this.$Message.error(res.data.errmsg);
					}
				}, (err) => {
					//do something
				})
			},
			handleSubmit() {
				this.$refs['evalFormData'].validate((valid) => {
					if(valid) {
						this.save();
					}
				})
			},
			deleteAssessmentReport(id) {
				const vm = this
				vm.$Modal.confirm({
					title: '删除确认',
					content: '确认删除吗？',
					onOk: () => {
						api(apiList.deleteAssessmentReport, {
							id: id
						}).then((res) => {
							if(res.status == 200) {
								this.viewSafetyEvalusion(this.evalFormData.assessmentId);
							} else {
								this.$Message.error('删除失败');
							}
						}, (err) => {
							//do something...
						})
					}
				})
			},
			validForm() {
				this.$refs['evalFormData'].validate();
			},
			setEvalReport(response, file, fileList) {
				this.evalFormData.assessmentFileId = response.data.list[0].id;
				this.evalFormData.assessmentFileName = response.data.list[0].submittedFileName;
				this.evalFormData.assessmentFileUrl = response.data.list[0].url;
				this.evalFormData.evalReport = 'ok';
				this.validForm();
				this.$Message.success('上传成功!');
			},
			setReformReport(response, file, fileList) {
				this.evalFormData.rectificationFileId = response.data.list[0].id;
				this.evalFormData.rectificationFileName = response.data.list[0].submittedFileName;
				this.evalFormData.rectificationFileUrl = response.data.list[0].url;
				this.evalFormData.reformReport = 'ok';
				this.validForm();
				this.$Message.success('上传成功!');
			},
			removeEvalReport(file, fileList) {
				if(fileList.length) {
					this.evalFormData.evalReport = 'ok';
				} else {
					this.evalFormData.evalReport = '';
				}
				this.validForm();
			},
			removeReformReport(file, fileList) {
				if(fileList.length) {
					this.evalFormData.reformReport = 'ok';
				} else {
					this.evalFormData.reformReport = '';
				}
				this.validForm();
			},
			checkButton(code) {
				if(this.authButton.indexOf(code) >= 0) {
					return true;
				} else {
					return false;
				}
			},
			assessmentTimeChange(fdate, type) {
				this.evalFormData.assessmentTime = fdate;
			},
			downloadFile(url,filename){
				let urlDownload = "/api/file/file/download?url=" + url + "&filename=" + filename;
                window.open(urlDownload);
			},
		},
		created() {
			this.$nextTick(() => {
				this.init();
			})
		},
	}
</script>
<style scoped="scoped" lang="less">
	.act-btn-group button {
		margin: 0 12px;
	}
</style>
