<template>
	<Layout>
		<Content>
			<Breadcrumb>
				<!-- <BreadcrumbItem>管理员(大数据办)</BreadcrumbItem> -->
				<BreadcrumbItem>{{title}}</BreadcrumbItem>
			</Breadcrumb>
			<Card>
				<Form ref="formValidate" inline :label-width="100" :model="serverData">
					<FormItem label="CPU：">
						<Select class="queryItem" clearable v-model="serverData.cpuCount">
							<Option value="2">2核</Option>
							<Option value="4">4核</Option>
							<Option value="8">8核</Option>
							<Option value="16">16核</Option>
						</Select>
					</FormItem>
					<FormItem label="内存：">
						<Select class="queryItem" clearable v-model="serverData.memorySize">
							<Option value="2">2GB</Option>
							<Option value="4">4GB</Option>
							<Option value="8">8GB</Option>
							<Option value="16">16GB</Option>
							<Option value="32">32GB</Option>
							<Option value="64">64GB</Option>
						</Select>
					</FormItem>
					<FormItem label="操作系统：">
						<Cascader :data="systemArray" @on-change="handleChange" trigger="hover"></Cascader>
					</FormItem>
					<FormItem label="所属网络：">
						<Select class="queryItem" clearable v-model="serverData.netScope">
							<Option v-for="netsItem in netScopeArray" :key="netsItem.id" :value="netsItem.code">{{netsItem.name}}</Option>
						</Select>
					</FormItem>
					<FormItem :label-width="20">
						<Button type="primary" @click="search">查询</Button>
						<!--<Button type="primary">刷新</Button>-->
					</FormItem>
				</Form>
				<hy-table ref="selection" :data="data" :columns="columns" :current="pageOption.current" :total="pageOption.total" :page-size="pageOption.pageSize" @on-change="pageChange" @on-page-size-change="changePageSize" show-sizer show-elevator/>
			</Card>
		</Content>
	</Layout>
</template>

<script>
	import api from '@/api/axiosApi'
	import softhardApiList from '@/api/softhardApiList'
	import { mapState } from 'vuex'
	export default {
		data() {
			return {
				title:this.$store.state.title,
				columns: [{
						type: 'index',
						title: '序号',
						width: 60,
						align: 'center'
					},
					{
						title: '服务器ID',
						key: 'serverId'
					},
					{
						title: '管理单位',
						key: 'orgname'
					},
					{
						title: '管理员',
						key: 'manageUname'
					},
					{
						title: 'CPU核数',
						key: 'cpuCount'
					},
					{
						title: '内存',
						key: 'memorySize'
					},
					{
						title: '操作系统',
						key: 'system'
					},
					{
						title: '所属网络',
						key: 'netScope'
					},
					{
						title: '开通时间',
						width: 220,
						align: 'center',
						key: 'openTime'
					},
					{
						title: '最近变更时间',
						width: 220,
						align: 'center',
						key: 'updateTime'
					},
					{
						title: '变更次数',
						key: 'changeCount',
						render: (h, params) => {
							const vm = this
							const num = params.row.changeCount
							const change = h('Button', {
								props: {
									type: 'primary',
									size: 'small'
								},
								domProps: {
									innerHTML: num
								},
								on: {
									click: () => {
										let originId = params.row.originId;
										this.$router.push({
											path: '/changeList/'+originId
										});	
									}
								}
							});
							if(params.row.changeCount>0){
								return h('div', [change]);
							}else{
								return h('div', params.row.changeCount);
							};
						}
					},
					{
						title: '操作',
						key: 'act',
						render: (h, params) => {
							const detail = h('Button', {
								props: {
									type: 'primary',
									size: 'small'
								},
								style: {
									display:this.checkButton('hardware_server_manage_detail')?'inline-block':'none'
								},
								on: {
									click: () => {
										let serverId = params.row.id;
										this.$router.push({
											path: '/server/detail/'+serverId
										});												
									}
								}
							}, '详情');
							return h('div', [detail])
						}
					}
				],
				data: [],
				serverData: {//查询参数
					cpuCount:"",
					memorySize:"",
					system:"",
					netScope:"",
				},
				pageOption: { //分页参数
					current: 1,
					total: 0,
					pageSize:10
				},
				systemArray: this.$store.state.systemArray, //操作系统列表
				sysVal: "",
				netScopeArray: this.$store.state.netScopeArray,//所属网络列表
			}
		},
		mounted() {
			this.servermanagerPage(); //查询服务分配管理分页
		},
		methods: {
			builderTree(r) {
				let _this = this;
				if(!r || r.length == 0) {
					return;
				};
				let itemArray = [];
				r.forEach(function(value, index) {
					let isChildOrg = true;
					if(!value.dtos || value.dtos.length == 0) {
						isChildOrg = false;
						value.children = [];
					};
					if(isChildOrg) {
						value.children = _this.builderTree(value.dtos);
					};
					let itemRow = {
						value: value.code,
						label: value.name,
						children: value.children,
					};
					itemArray.push(itemRow);
					return;
				});
				return itemArray;
			},
			handleChange(value, selecteddata) {
				if(selecteddata.length>0){
					let val = selecteddata.map(o => o.value).reverse();
					this.serverData.system = val[0];
				}else{
					this.serverData.system = "";
				}
			},
			pageChange(num) { //页码改变的回调
				this.pageOption.current = num;
				this.servermanagerPage();
			},
			changePageSize(num) { //切换每页条数时的回调
				this.pageOption.pageSize = num;
				this.servermanagerPage();
			},
			servermanagerPage() { // 查询服务分配管理分页
				this.data = [];
				let netisall = false;
				if(this.serverData.netScope=="-1"){
					this.serverData.netScope="";
					netisall = true;
				};
				var formData = {
					"data": { ...this.serverData
					}, //Object.assign({}, this.searchCondition),
					"pageNo": this.pageOption.current,
					"pageSize": this.pageOption.pageSize
				};
				api(softhardApiList.servermanagerPage, formData).then((res) => {
					if(res.status == 200 && res.data.data) {
						this.data = res.data.data.list;
						if(this.data.length>0){
							this.pageOption.pageSize = res.data.data.pageSize;
							this.pageOption.total = res.data.data.total;
							this.pageOption.current = res.data.data.pageNum;
							this.data.forEach(item=>{
								item.openTime = item.openTime.replace("-","年");
								item.openTime = item.openTime.replace("-","月");
								item.openTime = item.openTime.replace(" ","日 ");
								item.updateTime = item.updateTime.replace("-","年");
								item.updateTime = item.updateTime.replace("-","月");
								item.updateTime = item.updateTime.replace(" ","日 ");
							})
							if(netisall){
								this.serverData.netScope="-1";
							};
						};
					}
				}, (err) => {
					//dong something...
				})
			},
			search() {
				this.pageOption.current=1;
				if(this.serverData.cpuCount=="-1"){
					this.serverData.cpuCount="";
				};
				if(this.serverData.memorySize=="-1"){
					this.serverData.memorySize="";
				};
				this.servermanagerPage();
			},
			checkButton(code){
				if(this.authButton.indexOf(code) >= 0){
					return true;
				}else{
					return false;
				}
			}
		},
		computed: {
			...mapState([
			'authButton'
			])
		},
	}
</script>

<style lang='less'>
	.queryItem {
		width: 200px;
	}	
	
	.access-list {
		span.handle {
			margin: 0 5px;
			display: inline-block;
			cursor: pointer;
			&:hover {
				color: #57a3f3;
			}
		}
	}
</style>