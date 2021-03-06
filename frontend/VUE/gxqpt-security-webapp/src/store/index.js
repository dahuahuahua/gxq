/* eslint-disable */
// import Vue from 'vue'
// import Vuex from 'vuex'
import ApiList from '@/api/comApiList'
import softhardApiList from '@/api/softhardApiList'
import comApiList from '@/api/comApiList'
import api from '@/api/axiosApi'
Vue.use(Vuex)
// const roots = process.env.API_ROOT;
const store = new Vuex.Store({
	state: {
		alertText: '',
		title:'',
		isAlert: false,
        isShowMask: false,
		dataDictionary: {
			dbTypes: { //数据库类型（1 mysql，2 oracle，3 sqlserver，4 mongodb，5 hbase）
				1: 'mysql',
				2: 'oracle',
				3: 'sqlserver',
				4: 'mongodb',
				5: 'hbase'
			}
		},
		authButton: [],
		visibleForSelf:[],
		systemArray: [], //操作系统列表
		netScopeArray: [],//所属网络列表
		netWideData: [],//所属网络带宽
		myAppOrgAllData: [],//查询我所管理的所有启用的应用+服务(1：应用；2：服务；不传为全部)
		myServerData: [],//查询我所管理的所有启用的服务(1：应用；2：服务)
		myAppData: [],//查询我所管理的所有启用的应用
		myOrgPersonData:[],//我所在的单位人员名单
		logoutUrl: '/gxqpt-center/admin/index', //退出登陆链接
	},
	mutations: {
		GETUSERINFO(state) {
			//解析所有的token到此store下面。
			state.userInfo = loginController.getLoginUserInfo();
			state.userInfo.userId = state.userInfo.userid;
		},
		GETAUTHBUTTON(state,params){
			state.authButton = params;
		},
		FINDAPPVISIBLEFORSELF(state, params) {
			state.visibleForSelf = params;
		},
		CZXTFINDBYCODE(state, params) {
			state.systemArray = params;
		},
		SSWLFINDBYCODE(state, params) {
			state.netScopeArray = params;
		},
		WLKDFINDBYCODE(state, params) {
			state.netWideData = params;
		},
		FWLBBYADMIN(state, params) {
			state.myServerData = params;
		},
		ALLBBYADMIN(state, params) {
			state.myAppOrgAllData = params;
		},
		YYLBBYADMIN(state, params) {
			state.myAppData = params;
		},
		GETORGBYPOWER(state, params) {
			console.log(params);
			state.myOrgPersonData = params;
		},
	},
	actions: {
		getAuthButton({ commit, state }, resourceId) {//获取后端权限接口数据
			var arr = [];
			if (loginController.isLogined()) {
				api(ApiList.getOperateButton, {
					resourceId: resourceId,
					// userId: JSON.parse(sessionStorage._token).userid
					userId: state.userInfo.userId//本地测试
				}).then((res) => {
					if (res.data.errcode == 0) {
						res.data.data.forEach(function (item) {
							arr.push(item.code);
						})
						commit("GETAUTHBUTTON", arr);
					} else {
						console.log(res.data.errmsg)
					}
				})
			}
		},
		FindAppVisibleForSelf({ commit }) {//查询指定用户可见的应用列表 (安全保障平台)
			if (loginController.isLogined()) {
				api(ApiList.FindAppVisibleForSelf,{type: 1}).then((res) => {
					if (res.data.errcode == 0) {
						let arr = [];
						res.data.data.forEach(function (item) {
							arr.push({ name: item.name, appId: item.appId });
						})
						commit("FINDAPPVISIBLEFORSELF", arr);
					} else {
						console.log(res.data.errmsg)
					}
				})
			}
		},
		czxtFindByCode({ commit , dispatch }) {//获取操作系统列表
			api(softhardApiList.findDictionaryTree, {
				code: "ZDWH_czxt"
			}).then((res) => {
				if (res.status == 200 && res.data.data) {
					let systemList = res.data.data;
					let systemArray = [];
					systemArray = builderTree(systemList);
					commit("CZXTFINDBYCODE", systemArray);
				}
			}, (err) => {
				//dong something...
			})
			var builderTree = ( r => {
				let _this = this;
				if (!r || r.length == 0) {
					return;
				};
				let itemArray = [];
				r.forEach(function (value, index) {
					let isChildOrg = true;
					if (!value.dtos || value.dtos.length == 0) {
						isChildOrg = false;
						value.children = [];
					};
					if (isChildOrg) {
						value.children = builderTree(value.dtos);
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
			})
		},
		sswlFindByCode({ commit }) {//获取所属网络列表
			api(softhardApiList.dictionaryFindByCode, {
				code: "ZDWH_sswl"
			}).then((res) => {
				if (res.status == 200 && res.data.data) {
					commit("SSWLFINDBYCODE", res.data.data)
				}
			}, (err) => {
				//dong something...
			})
		},
		wlkdFindByCode({ commit }) {//获取所属网络带宽
			api(softhardApiList.dictionaryFindByTypeCode, {
				code: "ZDWH_wlkd"
			}).then((res) => {
				if (res.status == 200 && res.data.data) {
					commit("WLKDFINDBYCODE", res.data.data)
				}
			}, (err) => {
				//dong something...
			})
		},
		fwlbByAdmin({ commit },type) {//查询我所管理的所有启用的应用或服务(1：应用；2：服务)
			console.log(type);
			api(comApiList.findAppByAdminWithEnable, {
				type: type
			}).then((res) => {
				if (res.status == 200 && res.data.data) {
					if (!type) commit("ALLBBYADMIN", res.data.data);
					(type == 1) ? commit("YYLBBYADMIN", res.data.data) : commit("FWLBBYADMIN", res.data.data);
				}
			}, (err) => {
				//dong something...
			})
		},
		findOrgByPower({ commit }) {//查询我所管理的所有启用的应用或服务(1：应用；2：服务)
			api(comApiList.findOrgByPower).then((res) => {
				if (res.status == 200 && res.data.data) {
					let ids = [];
					res.data.data.forEach(item=>{
						ids.push(parseInt(item.id));
					})
					console.log(ids);
					api(comApiList.findEmpByOrgId, {orgIds: ids}).then((resp) => {
						if (resp.status == 200 && resp.data.data) {
							let data = [];
							resp.data.data.forEach(item => {
								data.push({ name: item.name, gxqptEmpId: item.gxqptEmpId });
							})
							commit("GETORGBYPOWER", data);
						}
					}, (err) => {
						//dong something...
					})
				}
			}, (err) => {
				//dong something...
			})
		},
	},
	modules: {}
})

export default store
