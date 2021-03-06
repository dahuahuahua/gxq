package com.hengyunsoft.platform.hardware.repository.manage.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.hengyunsoft.platform.hardware.entity.apply.domain.ApplyManageDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.OrgServerCountDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.PanelOrgCpuCountDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.PanelOrgMonthCpuDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.ServerCountDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.ServerManageChangeReqDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.ServerManageResDO;
import com.hengyunsoft.platform.hardware.entity.manage.domian.ServersCountByMonthDO;
import com.hengyunsoft.platform.hardware.entity.manage.po.ServerManage;

@Repository
public interface ServerManageMapper extends com.hengyunsoft.base.dao.BaseAllDao<Long, com.hengyunsoft.platform.hardware.entity.manage.po.ServerManage, com.hengyunsoft.platform.hardware.repository.manage.example.ServerManageExample> {
    /**
     * 查询服务分配管理分页
     * @param obj
     * @return
     */
    List pageNewest(@Param(value = "obj") ServerManage obj);

    /**
     * 查找变更历史
     * @param obj
     * @return
     */
    List findServerManageChange(@Param(value = "obj") ServerManageChangeReqDO obj);

    /**
     * 查询内存总容量
     * @return
     */
    ServerCountDO getAllMemorySize();

    /**
     * 查询磁盘总容量
     * @return
     */
    long getAllDiskSize();

    /**
     * 查询各单位服务器数量统计
     * @return
     */
    List<OrgServerCountDO> getOrgServersCount();

    /**
     * 查询每月服务器数量
     * @param startMonth
     * @param endMonth
     * @return
     */
    List<ServersCountByMonthDO> getServersCountByMonth(@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    /**
     * 查询存储管理，计算资源管理，带宽管理等数据
     * @param manage
     * @return
     */
    List<ServerManageResDO> getServerManageRes(ServerManageResDO manage);
    
    /**
	 * 获取所有的部门
	 * @return
	 */
    List<String> getOrgnamelist();
    
    /**
     * 网络带宽面板
     * 网络带宽面板  本月 统计 
     * thisMonth 本月（2018-01）
     * @return
     */
    List<Map> getThisMonthStatistics(@Param("thisMonth")Date thisMonth);
	
	 /**
     * 网络带宽面板
     * 网络带宽面板  各部门网络带宽统计 
     * thisMonth 本月（2018-01）
     * @return
     */
    List<Map> getEachOrgNetWorkBandwidthMonthStatistics(@Param("thisMonth")Date thisMonth);
	
	/**
	 * 网络带宽面板
	 * 网络带宽面板  各部门网络带宽统计 
	 * thisMonth 本月（2018-01）
	 * @return
	 */
    List<Map> getEachOrgNetWorkBandwidthYearStatistics(@Param("thisMonth")Date thisMonth);

    /**
     * 查询cpu总和
     * @return
     */
    int getAllSumOfCpu();

    /**
     * 查询本月cpu核数统计情况
     * @param startTime
     * @param endTime
     * @return
     */
    int getMonthAddCpuRes(@Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 本月cpu回收情况
     * @param startTime
     * @param endTime
     * @return
     */
    int getMonthSubCpuRes(@Param("startTime")String startTime, @Param("endTime")String endTime);

    /**
     * 查询各单位cpu资源情况
     * @return
     */
    List<PanelOrgCpuCountDO> getPanelOrgCpuRes();

    /**
     * 查询各单位每月cpu资源
     * @param startMonth
     * @param endMonth
     * @return
     */
    List<PanelOrgMonthCpuDO> getPanelMonthOrgCpuRes(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    /**
     * 获取所有基本的服务器信息
     * @return
     */
	List<ApplyManageDO> findServerManage();
}