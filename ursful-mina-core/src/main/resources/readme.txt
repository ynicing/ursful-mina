1.packet
[FF FF FF FF] [FF FF] [FF ... FF]
header size    cmd    data
2.cluster
[FF FF FF FF] [FF FF] [FS]     [FS]  [Data]
header size    cmd    [fromCid  data]
                      data


3.cluster 监控数据，数据同步：客户端状态同步，客户端[服务信息同步，断掉重连其他服务器]
微服务实时向服务端发送数据，服务端转发至所有服务端
缓存：
a.系统信息
b.在线状态[id@domain]

4.cluster 操作，数据交互：关闭定时任务？cid=id@domain
微服务向服务端发送数据，服务端转发至对应服务端，并处理。处理完，反馈至源服务端！

ursful.clusters=192.168.0.1:8080,192.168.0.1:8081


5.集群的时候，数据不需要加密，兼容原有的服务端加密方式。
  服务器增加sid，sid-cluster， 服务器名称发布到client 同步。

6.完成了集群消息的传递， 如 C1->S1->CS1->CS2->S2->C2
  上线状态转发，在线状态缓存与集群之间的通讯转发。

  集群内部消息转发 直接是客户端接收的格式， 但是，从客户端转发过来的消息，需要通过handler处理，然后，再转发！
  消息直接中转。


 7.集群中客户端的设计： 连接成功后，发起基础信息info， 维持心跳；后续只接收数据，将接收到的数据进行转发到。


 8. Master 设定。
 当第一台服务器启动时，则为master， 其他主机按加入id排序。

 master 定时发送请求， 请求其他主机连接数，再给其他主机分配id。
 当master 断开后，则其他主机最接近 master的变为新的主机。



 10.
 增加id唯一性验证，否则断开连接。

 关键字 system, all 不能够被使用。

 11.********************
 服务器断线 移除客户端信息，并通知其他客户端。

 12. 时间同步方案

 服务器id序列号》时间

 采用数据库时间同步方案。因为数据库连接唯一。 如果没有获取时间，则不启动。

 系统启动后，给每个定时任务分配时间。（定时任务最早的先执行）；


############################
presence info 有可能多次！！！比如test1 登录server1，server1向 test1 发送客户端状态； 当集群与server2连接时，将server2的客户端状态由再次发送给了test1.