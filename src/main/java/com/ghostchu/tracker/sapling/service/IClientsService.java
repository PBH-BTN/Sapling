package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Clients;
import com.ghostchu.tracker.sapling.vo.ClientsVO;
import com.github.yulichang.base.MPJBaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IClientsService extends MPJBaseService<Clients> {

    List<Clients> getAllClients();

    ClientsVO toVO(Clients entity);

    Clients getClientById(Long id);

    Clients deleteClientById(Long id);

    void createClient(String name, String agentPattern, String peerPattern, String comment);

    Clients updateClient(Clients client);
}
