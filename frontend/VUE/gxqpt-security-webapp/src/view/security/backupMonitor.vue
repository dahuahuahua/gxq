<template>
	<Layout>
		<Content>
			<Breadcrumb>
				<BreadcrumbItem>安全保障平台</BreadcrumbItem>
				<BreadcrumbItem>备份监控</BreadcrumbItem>
			</Breadcrumb>
			<Card>
				<Form ref="formValidate" :model="formData" inline :label-width="120">
					<FormItem label="备份系统名称">
						<Select v-model="formData.applicationName" @on-query-change="applicationNameChange" style="min-width: 150px;">
							<Option v-for="(item,index) of visibleForSelf" :value="item.name" :key="index" :label="item.name"></Option>
						</Select>
					</FormItem>
					<FormItem label="备份名称" :label-width="80">
						<Select ref="name" v-model="formData.name" style="min-width: 150px;" @on-change="nameChange" clearable>
							<Option v-for="(item,index) of BackupNames" :value="item" :key="index">{{item}}</Option>
						</Select>
					</FormItem>
					<FormItem :label-width="30">
            <Button type="primary" @click="getBackupLog">查询</Button>
          </FormItem>
				</Form>
				<div id='calendar' style="width: 100%;height: 700px;"></div>
				<div class="clearfix"> </div>
			</Card>
		</Content>
	</Layout>
</template>
<script>
	import $ from 'jquery'
	import calendar from '@static/Simple-Calendar' //使用日历模板
	import api from '@/api/axiosApi'
	import apiList from '@/api/securityApiList'
	import { mapState } from 'vuex'
	export default {
		data() {
			return {
				systemNames: [],
				BackupNames: [],
				myCalendar: null,
				startTime:'',
				formData: {
					yearMonth: '',
					applicationName:'',
					name:'',
				}
			}
		},
		mounted() {
			console.log(this.visibleForSelf);
			this.init();
		},
		computed: {
			...mapState([//引入公共系统名称列表
				'visibleForSelf'
			])
		},
		methods: {
			init(){
				var _this = this;
				_this.simpleCalendar().then(() => {
				  _this.getBackupLog();
				})
				/*_this.myCalendar = new SimpleCalendar('#calendar', {//日历初始化设置
					showFestival: false,
					showHoliday: false,
					showSolarTerm: false,
					showLunarFestival: false,
					showMark: false,
					height: '720px',
					callBack: function(year, month) {
						// _this.formData.yearMonth = year + '-' + month;
						_this.getBackupLog();
					}
				});*/
				
			},
			simpleCalendar() {
			  var _this = this;
			  let calendar = new Promise(function(resolve,reject){
			    _this.myCalendar = new SimpleCalendar('#calendar', {//日历初始化设置
            showFestival: false,
            showHoliday: false,
            showSolarTerm: false,
            showLunarFestival: false,
            showMark: false,
            height: '720px',
            callBack: function(year, month) {
              _this.getBackupLog();
            }
          });
          if(_this.myCalendar){
            resolve("日历初始化成功");
          }
        });
        return calendar;
			},
			getbySystemName(){//根据选择的系统名，获取子系统名称列表
				api(apiList.getbySystemName,{systemName:this.formData.applicationName}).then((res) => {
					if(res.status == 200) {
						this.BackupNames = res.data.data;
						this.startTime = '';
						this.formData.name = '';
						// this.getBackupLog();
					}
				})
			},
			applicationNameChange(val) {//系统名称选择
				this.BackupNames = [];//清空列表值，避免select的数据缓存造成的显示问题bug
				this.getbySystemName()
			},
			nameChange(val) {//子系统名称选择
				console.log(val);
				console.log(this.BackupNames);
				if(val){
					this.formData.name = val;
//					this.getBackupLog()
				}/*else{
					$('.sc-item:not(.sc-othermenth)').removeClass('sc-backup');
					$('.sc-item:not(.sc-othermenth)').removeClass('sc-backup-err');
				}*/
      },
      fmtDate (obj) {
        var date =  new Date(obj);
        var y = 1900+date.getYear();
        var m = "0"+(date.getMonth()+1);
        var d = "0"+date.getDate();
        return y+"-"+m.substring(m.length-2,m.length)+"-"+d.substring(d.length-2,d.length);
      },
			getBackupLog() {//获取具体数据，并渲染日历表
				this.formData.yearMonth = this.myCalendar.getYearMonth();
        //当前时间
        var d = new Date();
				var nowYear = d.getFullYear();
				var nowMonth = d.getMonth() + 1;
				var nowday = d.getDate();
        // 显示时间
				var year = parseInt(this.formData.yearMonth.split('-')[0]);
        var month = parseInt(this.formData.yearMonth.split('-')[1]);
        var flag = false;
				if(this.startTime!=''){
					if(parseInt(month) < parseInt(this.startTime.split('-')[1]))return;
				}
				api(apiList.getBackupLog, this.formData).then((res) => {
					if(res.status == 200) {
						if(res.data.errcode != 0) {
							this.$Message.error(res.data.errmsg);
						} else {
						  if(!res.data.data){
						    this.$Message.error(res.data.errmsg);
						    $('.sc-item:not(.sc-othermenth)').removeClass('sc-backup');
                $('.sc-item:not(.sc-othermenth)').removeClass('sc-backup-err');
						    return false;
						  }
							var arr = [];
              this.startTime = this.fmtDate(res.data.data.startTime);
              // 获取时间
              var startYear = parseInt(this.startTime.split('-')[0]);
              var startMonth = parseInt(this.startTime.split('-')[1]);
              var startday = parseInt(this.startTime.split('-')[2]);
							if(!!res.data.data.logList){//当数据不存在或为空数组
								res.data.data.logList.forEach(function(item) {
									arr.push(Number(item.createTime.split(' ')[0].split('-')[2]));
								})
								$.each($('.sc-item:not(.sc-othermenth)'), function(idx, item) {
                  if ($(item).find('.day').text() <= nowday) { // 小于今天的显示日期
                    if (year == startYear && year <= nowYear && month <= nowMonth) {
                      $(item).addClass('sc-backup-err');
                      arr.forEach(function(num) { // 渲染数据
                        if($(item).find('.day').text() == num) {
                          $(item).removeClass('sc-backup-err');
                          $(item).addClass('sc-backup');
                        }
                      })
                      if (year == startYear && month == startMonth && $(item).find('.day').text() < startday) {
                        $(item).removeClass('sc-backup-err');
                      }
                    }
                  } else { // 大于今天的显示日期
                    if (year == startYear && month < nowMonth){
                      $(item).addClass('sc-backup-err');
                      arr.forEach(function(num) { // 渲染数据
                        if($(item).find('.day').text() == num) {
                          $(item).removeClass('sc-backup-err');
                          $(item).addClass('sc-backup');
                        }
                      })
                      if (year == startYear && month == startMonth && $(item).find('.day').text() < startday) {
                        $(item).removeClass('sc-backup-err');
                      }
                    }
                  }
								})
							}
						}
					}
				}, (err) => {
				})
			}
		}
	}
</script>
<style lang="less" scoped="scoped">
	#calendar /deep/ .sc-item.sc-backup {
		background: #a3fda3;
	}
	
	#calendar /deep/ .sc-item.sc-backup-err {
		background: #ff968e;
	}
</style>
