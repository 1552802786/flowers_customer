package com.yuangee.flower.customer.db;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Created by developerLzh on 2017/6/20 0020.
 */
interface IDatabaseImp<M, K> {
    boolean insert(@NotNull M m);

    boolean insertOrReplace(@NotNull M m);

    boolean insertInTx(@NotNull List<M> list);

    boolean insertOrReplaceInTx(@NotNull List<M> list);

    boolean delete(@NotNull M m);

    boolean deleteByKey(@NotNull K key);

    boolean deleteInTx(@NotNull List<M> list);

    boolean deleteAll();

    boolean update(@NotNull M m);

    boolean updateInTx(@NotNull List<M> list);

    M load(@NotNull K key);

    List<M> loadAll();

    boolean refresh(@NotNull M m);

    void runInTx(@NotNull Runnable runnable);

    AbstractDao<M, K> getAbstractDao();

    QueryBuilder<M> queryBuilder();

    List<M> queryRaw(@NotNull String where, @NotNull String... selectionArg);

    Query<M> queryRawCreate(@NotNull String where, @NotNull Object... selectionArg);

    Query<M> queryRawCreateListArgs(@NotNull String where, @NotNull Collection<Object> selectionArg);
}
