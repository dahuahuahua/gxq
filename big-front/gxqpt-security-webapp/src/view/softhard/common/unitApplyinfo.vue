<template>
	<Card :bordered="false">
		<p slot="title">单位申请信息</p>
		<div style="text-align:center">
			<Form :model="applyManage" :label-width="100" class="clearfix">
				<Row :gutter="40">
					<Col span="12">
					<FormItem label="申请单位：">
						<Input v-model="applyManage.applyOrgname" disabled></Input>
					</FormItem>
					</Col>
					<Col span="12">
					<FormItem label="申请人：">
						<Input v-model="applyManage.applyUname" disabled></Input>
					</FormItem>
					</Col>
					<Col span="12">
					<FormItem label="申请人邮箱：">
						<Input v-model="applyManage.applyEmail" disabled></Input>
					</FormItem>
					</Col>
				</Row>
			</Form>
			<table class="hostTab">
				<thead>
					<tr>
						<th rowspan="2" width="60">序号</th>
						<th rowspan="2" width="60">CPU核数</th>
						<th rowspan="2" width="60">内存</th>
						<th rowspan="2">操作系统</th>
						<th rowspan="2">所属网络</th>
						<th rowspan="2" width="60">网络带宽</th>
						<th rowspan="2" width="80">申请数量</th>
						<th colspan="2" width="120">磁盘列表</th>
					</tr>
					<tr>
						<th>磁盘名称</th>
						<th>磁盘大小</th>
					</tr>
				</thead>
				<tbody v-for="(serveItem,index) in  applyManage.applyServer">
					<tr>
						<td :rowspan="serveItem.disks.length">{{index+1}}</td>
						<td :rowspan="serveItem.disks.length">{{serveItem.cpuCount}}</td>
						<td :rowspan="serveItem.disks.length">{{serveItem.memorySize}}</td>
						<td :rowspan="serveItem.disks.length">{{serveItem.systemName}}</td>
						<td :rowspan="serveItem.disks.length">{{serveItem.netScopeName}}</td>
						<td :rowspan="serveItem.disks.length">{{serveItem.netWide}}</td>
						<td :rowspan="serveItem.disks.length">{{serveItem.applyCount}}</td>
						<td>{{serveItem.disks[0].diskName}}</td>
						<td>{{serveItem.disks[0].diskSize}}</td>
					</tr>
					<tr v-for="(disks,index) in  serveItem.disks" v-if="index>0">
						<td>{{disks.diskName}}</td>
						<td>{{disks.diskSize}}</td>
					</tr>
				</tbody>
			</table>
			<Row style="margin-top: 20px;">
				<Form :label-width="100" :model="applyManage">
					<FormItem label="申请原因：">
						<Input type="textarea" :autosize="true" v-model="applyManage.applyReason" disabled>
						</Input>
					</FormItem>
					<FormItem class="download" label="附件说明：" >
						<p class="marginBottom" v-for="fileItem in  applyManage.attachment">
							<Input type="text" style="width: 400px;" v-model="fileItem.fileName"  disabled></Input>
							<Button type="primary" @click="downLoad(fileItem.fileUrl,fileItem.fileName)">下载</Button>
						</p>
					</FormItem>
				</Form>
			</Row>
		</div>
	</Card>
</template>

<script>
	export default {
		props:["applyManageData"],
		data() {
			return {
				applyManage:{}
			}
		},
		mounted(){
			this.getViewInfo();
		},
		methods:{
			getViewInfo(){
				let index = 1
				this.applyManage=this.applyManageData;
				this.applyManage.applyServer.forEach(item => {
					item.disks.forEach(diskItem=>{
						let nameType = ''
						if (diskItem.diskType === 2) {
							nameType = '系统盘'
						} else {
							nameType = '数据磁盘' + index
							index ++
						}
						diskItem.diskName = nameType;
					})
				});
			},
			downLoad(url,filename){//下载
				let urlDownload = "/api/file/file/download?url=" + url + "&filename=" + filename;
				window.open(urlDownload);
			},
		},
		watch:{
			applyManageData:{
				handler(newValue, oldValue) {
					this.getViewInfo();　　
				},
				deep: true　　
			}
		}
	}
</script>
<style lang="less" type="text/css" scoped>
	.marginBottom{
		margin-bottom: 10px;
		text-align: left;
	}
	.hostTab {
		width: 100%;
		table-layout: fixed;
		border-collapse: collapse;
		border-spacing: 0;
		th {
			background: #f5f5f5;
			padding: 5px 9px;
			border: 1px solid #ddd;
			font-weight: normal;
		}
		td {
			padding: 5px 9px;
			border: 1px solid #ddd;
			text-align: center;
			background: #fff;

		}
	}
</style>
