/**
 * 注by wj：回答了一个终极迷思——API层的模型和领域层的模型有什么差异需要去隔离？
 * 1.引导进一步消费的方式不同，。领域模型供的函数/方法，而API层是HATEOAS的link方式，这就导致了表现层和领域层模型的结构差异。
 *      譬如，领域层可能有“id+description+10个association”，但在表现层只有description和identity两个字段，其余通过link表示
 * 2.在领域模型中的A关联，是直接了当出现在模型里的，但在表现层你可能希望不通过link，而是也直接在数据模型中(甚至，可能在数据层有简单的概述，但也有一个link指向详细的数据)，因此这也是一种差异。
 *
 * 但是注意！表现层给出的模型，依然应该遵循领域模型的对象关系，而不是另立一套对象关系：
 * 譬如企业和雇员之间的关系，在领域层是“企业->员工list”，在表现层可以是按需省流量的"员工list=null"（见TransactionModel），但不应该在表现层就变成”企业&员工list“或者"企业->部门->员工"，尤其前者是RPC思维的返回结果，并非数据思维
 */
package reengineering.ddd.accounting.api.representation;