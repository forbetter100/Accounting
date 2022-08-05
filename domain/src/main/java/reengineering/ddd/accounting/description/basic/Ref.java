package reengineering.ddd.accounting.description.basic;

/**
 * 注解：给外键id这种特殊的“值对象”建模
 */
public record Ref<Identity>(Identity id) {
}
