package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Clients;
import com.ghostchu.tracker.sapling.mapper.ClientsMapper;
import com.ghostchu.tracker.sapling.service.IClientsService;
import com.ghostchu.tracker.sapling.vo.ClientsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class ClientsServiceImpl extends MPJBaseServiceImpl<ClientsMapper, Clients> implements IClientsService {

    @Override
    public List<Clients> getAllClients() {
        return list(new QueryWrapper<Clients>().orderByAsc("id"));
    }

    @Override
    public ClientsVO toVO(Clients entity) {
        ClientsVO vo = new ClientsVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setAgentPattern(entity.getAgentPattern());
        vo.setPeerPattern(entity.getPeerPattern());
        vo.setAllowed(entity.isAllowed());
        vo.setComment(entity.getComment());
        return vo;
    }

    @Override
    public Clients getClientById(Long id) {
        return getOne(new QueryWrapper<Clients>().eq("id", id));
    }

    @Override
    public Clients deleteClientById(Long id) {
        Clients client = getClientById(id);
        removeById(id);
        return client;
    }

    @Override
    public void createClient(String name, String agentPattern, String peerPattern, String comment) {
        Clients client = new Clients();
        client.setName(name);
        client.setAgentPattern(agentPattern);
        client.setPeerPattern(peerPattern);
        client.setComment(comment);
        save(client);
    }

    @Override
    public Clients updateClient(Clients client) {
        updateById(client);
        return client;
    }
}
