package yz;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import yz.hbase.config.HBaseConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * author: liuyazong
 * datetime: 2017/7/14 下午2:46
 */
@Slf4j
public class App {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HBaseConfig.class);
        context.start();
        Connection connection = context.getBean(Connection.class);
//        getOne(connection);
//        getMany(connection);
//        putOne(connection);
//        putMany(connection);
//        getOne(connection);
//        getMany(connection);
//        delete(connection);

//        checkAndPut(connection);
//        checkAndDelete(connection);
        Scan scan = new Scan()
                .addColumn(Bytes.toBytes("basic"), Bytes.toBytes("mobile"));
        try (Table user = connection.getTable(TableName.valueOf("user"));
             ResultScanner scanner = user.getScanner(scan);) {
            for (Result result :
                    scanner) {
                log.debug("result:{}", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void checkAndDelete(Connection connection) {
        try (Table user = connection.getTable(TableName.valueOf("user"));) {
            Get get0 = new Get(Bytes.toBytes("u0"));
            get0.addColumn(Bytes.toBytes("basic"), Bytes.toBytes("mobile"));
            Result result = user.get(get0);
            log.debug("before checkAndDelete:result={}", Bytes.toString(result.getValue(Bytes.toBytes("basic"), Bytes.toBytes("mobile"))));
            Delete u0 = new Delete(Bytes.toBytes("u0"));
            u0.addColumn(Bytes.toBytes("basic"), Bytes.toBytes("mobile"));
            boolean b = user.checkAndDelete(Bytes.toBytes("u0"), Bytes.toBytes("basic"), Bytes.toBytes("mobile"), Bytes.toBytes("18500000000"),
                    u0);
            result = user.get(get0);
            log.debug("after checkAndDelete:{},result={}", b, Bytes.toString(result.getValue(Bytes.toBytes("basic"), Bytes.toBytes("mobile"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkAndPut(Connection connection) {
        try (Table user = connection.getTable(TableName.valueOf("user"));) {
            Get get0 = new Get(Bytes.toBytes("u0"));
            get0.addColumn(Bytes.toBytes("basic"), Bytes.toBytes("mobile"));
            Result result = user.get(get0);
            log.debug("before checkAndPut:result={}", Bytes.toString(result.getValue(Bytes.toBytes("basic"), Bytes.toBytes("mobile"))));
            Put u0 = new Put(Bytes.toBytes("u0"));
            u0.add(CellUtil.createCell(Bytes.toBytes("u0"), Bytes.toBytes("basic"), Bytes.toBytes("mobile"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes("18500000001")));
            boolean b = user.checkAndPut(Bytes.toBytes("u0"), Bytes.toBytes("basic"), Bytes.toBytes("mobile"), Bytes.toBytes("18500000000"),
                    u0);
            result = user.get(get0);
            log.debug("after checkAndPut:{},result={}", b, Bytes.toString(result.getValue(Bytes.toBytes("basic"), Bytes.toBytes("mobile"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void delete(Connection connection) {


        try (Table user = connection.getTable(TableName.valueOf("user"));) {
            Delete u0 = new Delete(Bytes.toBytes("u0"));
            user.delete(u0);

            Get gu0 = new Get(Bytes.toBytes("u0"));
            Result result = user.get(gu0);
            log.debug("after delete u0,get u0:{}", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getOne(Connection connection) {
        try (Table user = connection.getTable(TableName.valueOf("user"));) {

            //get one ,all columns
            Get u0 = new Get(Bytes.toBytes("u0"));
            Result result = user.get(u0);
            log.debug("get u0={},basic:mobile={},basic:password={},ext:addr={},ext:id_no={}", u0, Bytes.toString(result.getValue(Bytes.toBytes("basic"), Bytes.toBytes("mobile"))),
                    Bytes.toString(result.getValue(Bytes.toBytes("basic"), Bytes.toBytes("password"))),
                    Bytes.toString(result.getValue(Bytes.toBytes("ext"), Bytes.toBytes("addr"))),
                    Bytes.toString(result.getValue(Bytes.toBytes("ext"), Bytes.toBytes("id_no"))));

            //get one ,one column
            u0.addColumn(Bytes.toBytes("ext"), Bytes.toBytes("id_no"));
            result = user.get(u0);
            log.debug("get u0={},ext:id_no={}", u0, Bytes.toString(result.value()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getMany(Connection connection) {
        try (Table user = connection.getTable(TableName.valueOf("user"));) {

            //get many
            List<Get> gets = new ArrayList<>();
            Get u2 = new Get(Bytes.toBytes("u2"));
            u2.addColumn(Bytes.toBytes("basic"), Bytes.toBytes("mobile"));
            gets.add(u2);
            gets.add(new Get(Bytes.toBytes("u3")));
            Result[] results = user.get(gets);
            log.debug("getMany gets={},result={}", gets, Arrays.asList(results));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void putOne(Connection connection) {
        try (Table user = connection.getTable(TableName.valueOf("user"));) {
            Put u0 = new Put(Bytes.toBytes("u0"));
            String s = UUID.randomUUID().toString();
            u0.add(CellUtil.createCell(Bytes.toBytes("u0"), Bytes.toBytes("basic"), Bytes.toBytes("mobile"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes("18500000000")));
            u0.add(CellUtil.createCell(Bytes.toBytes("u0"), Bytes.toBytes("basic"), Bytes.toBytes("password"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(s)));
            u0.add(CellUtil.createCell(Bytes.toBytes("u0"), Bytes.toBytes("ext"), Bytes.toBytes("addr"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(s)));
            u0.add(CellUtil.createCell(Bytes.toBytes("u0"), Bytes.toBytes("ext"), Bytes.toBytes("id_no"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(s)));
            user.put(u0);
            log.debug("putOne:{}", u0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void putMany(Connection connection) {
        try (Table user = connection.getTable(TableName.valueOf("user"));) {

            List<Put> puts = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                Put ui = new Put(Bytes.toBytes("u" + i));
                String s = UUID.randomUUID().toString();
                ui.add(CellUtil.createCell(Bytes.toBytes("u" + i), Bytes.toBytes("basic"), Bytes.toBytes("mobile"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(String.valueOf(18500000001L + i))));
                ui.add(CellUtil.createCell(Bytes.toBytes("u" + i), Bytes.toBytes("basic"), Bytes.toBytes("password"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(s)));
                ui.add(CellUtil.createCell(Bytes.toBytes("u" + i), Bytes.toBytes("ext"), Bytes.toBytes("addr"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(s)));
                ui.add(CellUtil.createCell(Bytes.toBytes("u" + i), Bytes.toBytes("ext"), Bytes.toBytes("id_no"), System.currentTimeMillis(), KeyValue.Type.Put.getCode(), Bytes.toBytes(s)));
                puts.add(ui);
            }
            user.put(puts);
            log.debug("putMany:{}", puts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
