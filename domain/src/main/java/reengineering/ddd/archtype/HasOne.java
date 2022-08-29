package reengineering.ddd.archtype;

/**
 * remark by wj:
 * 有HasMany抽象“一对多的”关联，就会有HasOne抽象“一对一”的关联
 */
public interface HasOne<E extends Entity<?, ?>> {
    E get();
}
