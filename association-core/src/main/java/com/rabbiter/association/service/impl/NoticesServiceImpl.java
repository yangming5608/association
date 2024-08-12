package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.dao.NoticesDao;
import com.rabbiter.association.service.NoticesService;
import com.rabbiter.association.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("noticesService")
public class NoticesServiceImpl implements NoticesService {

    @Autowired
    private NoticesDao noticesDao;

	@Autowired
	private TeamsDao teamsDao;

    @Override
    @Transactional
    public void add(Notices notices) {

        noticesDao.insert(notices);
    }

    @Override
    @Transactional
    public void update(Notices notices) {

        noticesDao.updateById(notices);
    }

    @Override
    @Transactional
    public void delete(Notices notices) {

        noticesDao.deleteById(notices);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Notices getOne(String id) {

        Notices notices = noticesDao.selectById(id);

        return notices;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Notices> getSysNotices(){

        List<Notices> list = noticesDao.qrySysNotices();

        return list;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Notices> getManNotices(String manId){

        List<Notices> list = noticesDao.qryManNotices(manId);

        return list;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Notices> getMemNotices(String memId){

        List<Notices> list = noticesDao.qryMemNotices(memId);

        return list;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageAll(Long pageIndex, Long pageSize, String title, String teamName){

        Page<Map<String, Object>>  page =
                noticesDao.qryPageAll(new Page<Map<String, Object>>(pageIndex, pageSize), title, teamName);

        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageById(Long pageIndex, Long pageSize, String userId, String title, String teamName){

        Page<Map<String, Object>>  page =
                noticesDao.qryPageById(new Page<Map<String, Object>>(pageIndex, pageSize), userId, title, teamName);

        return parsePage(page);
    }

    /**
     * 查询列表结果转换
     * @param notices
     * @return
     */
    public List<Map<String, Object>> parseList(List<Notices> notices){

        List<Map<String, Object>> resl = new ArrayList<Map<String, Object>>();

        for (Notices notice : notices) {

            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("id", notice.getId());
            temp.put("title", notice.getTitle());
            temp.put("detail", notice.getDetail());
            temp.put("createTime", notice.getCreateTime());

            if(StringUtils.isNotNullOrEmpty(notice.getTeamId())){

                Teams teams = teamsDao.selectById(notice.getTeamId());
                temp.put("teamId", notice.getTeamId());
                temp.put("teamName", teams.getName());
                temp.put("isTeam", true);
            }else{

                temp.put("isTeam", false);
            }

            resl.add(temp);
        }

        return resl;
    }

    /**
     * 转化分页查询的结果
     */
    public PageData parsePage(Page<Map<String, Object>> p) {

        PageData pageData = new PageData(p.getCurrent(), p.getSize(), p.getTotal(), p.getRecords());

        return pageData;
    }
}