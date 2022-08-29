package reengineering.ddd.mybatis.database;

import reengineering.ddd.archtype.Entity;
import reengineering.ddd.archtype.HasMany;
import reengineering.ddd.archtype.Many;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * 其实Many、HasMany的实现完全可以交给业务来实现，只是这里抽取了一些公共的逻辑，譬如
 * 1. 将Iterator的操作通过BatchIterator固化下来（以提高性能，避免业务自己真的每次都去问）
 * 2. 明确subCollection方法返回的，就不再是数据库版本，而是内存版本
 * 3. 而findAll和findByIdentity就声明为final，将具体过程再通过新的abstract方法交给业务子类实现
 * @param <Id>
 * @param <E>
 */
public abstract class EntityList<Id, E extends Entity<Id, ?>> implements Many<E>, HasMany<Id, E> {
    @Override
    public final Many<E> findAll() {
        return this;
    }

    @Override
    public final Optional<E> findByIdentity(Id identifier) {
        return Optional.ofNullable(findEntity(identifier));
    }

    /**
     * remark by wj:
     * 在subCollection的时候，就会从无限性质的数据库版本，切换为有限的内存版本
     */
    @Override
    public final Many<E> subCollection(int from, int to) {
        return new reengineering.ddd.mybatis.memory.EntityList<>(findEntities(from, to));
    }


    @Override
    public final Iterator<E> iterator() {
        return new BatchIterator();
    }
    /**
     *
     * remark by wj:
     * 数据库版本的EntityList的迭代器。之所以不是直接让业务自己实现，是因为这里想通过每次从数据库读一页的方式，避免真的每次从数据库查一行数据的低效。
     * 而真正的读取数据（也包括size），才是交给子类自己实现的
     */
    private class BatchIterator implements Iterator<E> {

        private Iterator<E> iterator;
        private int size;
        private int current = 0;

        public BatchIterator() {
            this.size = size();
            this.iterator = nextBatch();
        }

        private Iterator<E> nextBatch() {
            return subCollection(current, Math.min(current + batchSize(), size)).iterator();
        }

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public E next() {
            if (!iterator.hasNext()) iterator = nextBatch();
            current++;
            return iterator.next();
        }
    }

    protected int batchSize() {
        return 100;
    }

    protected abstract List<E> findEntities(int from, int to);

    protected abstract E findEntity(Id id);
}
