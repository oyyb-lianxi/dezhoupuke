package com.example.depu.manager;

import com.example.depu.model.Player;
import com.example.depu.model.Table;
import com.example.depu.model.Tournament;
import com.example.depu.model.ExchangeItem;
import com.example.depu.model.ExchangeRequest;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceManager {
    private static PersistenceManager instance;
    private String dataDirectory;

    private PersistenceManager() {
        dataDirectory = "data";
        new File(dataDirectory).mkdirs();
    }

    public static synchronized PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    // 保存玩家数据
    public void savePlayers(List<Player> players) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDirectory + "/players.dat"))) {
            oos.writeObject(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载玩家数据
    public List<Player> loadPlayers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDirectory + "/players.dat"))) {
            return (List<Player>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 保存赛事数据
    public void saveTournaments(List<Tournament> tournaments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDirectory + "/tournaments.dat"))) {
            oos.writeObject(tournaments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载赛事数据
    public List<Tournament> loadTournaments() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDirectory + "/tournaments.dat"))) {
            return (List<Tournament>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 保存兑换项目数据
    public void saveExchangeItems(List<ExchangeItem> exchangeItems) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDirectory + "/exchange_items.dat"))) {
            oos.writeObject(exchangeItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载兑换项目数据
    public List<ExchangeItem> loadExchangeItems() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDirectory + "/exchange_items.dat"))) {
            return (List<ExchangeItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 保存兑换申请数据
    public void saveExchangeRequests(List<ExchangeRequest> exchangeRequests) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDirectory + "/exchange_requests.dat"))) {
            oos.writeObject(exchangeRequests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载兑换申请数据
    public List<ExchangeRequest> loadExchangeRequests() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDirectory + "/exchange_requests.dat"))) {
            return (List<ExchangeRequest>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 备份数据
    public void backupData() {
        String backupDirectory = dataDirectory + "/backup_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        new File(backupDirectory).mkdirs();

        // 复制所有数据文件到备份目录
        copyFile(dataDirectory + "/players.dat", backupDirectory + "/players.dat");
        copyFile(dataDirectory + "/tournaments.dat", backupDirectory + "/tournaments.dat");
        copyFile(dataDirectory + "/exchange_items.dat", backupDirectory + "/exchange_items.dat");
        copyFile(dataDirectory + "/exchange_requests.dat", backupDirectory + "/exchange_requests.dat");
    }

    // 复制文件
    private void copyFile(String source, String destination) {
        try {
            File sourceFile = new File(source);
            if (sourceFile.exists()) {
                FileInputStream fis = new FileInputStream(sourceFile);
                FileOutputStream fos = new FileOutputStream(destination);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fis.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 自动备份
    public void scheduleAutoBackup() {
        // 每30秒备份一次
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                    backupData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 清理过期备份
    public void cleanupOldBackups() {
        File[] backupDirs = new File(dataDirectory).listFiles(File::isDirectory);
        if (backupDirs != null) {
            for (File dir : backupDirs) {
                if (dir.getName().startsWith("backup_")) {
                    long age = System.currentTimeMillis() - dir.lastModified();
                    if (age > 90 * 24 * 60 * 60 * 1000) { // 90天
                        deleteDirectory(dir);
                    }
                }
            }
        }
    }

    // 删除目录
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    // 恢复数据
    public void restoreData(String backupDirectory) {
        copyFile(backupDirectory + "/players.dat", dataDirectory + "/players.dat");
        copyFile(backupDirectory + "/tournaments.dat", dataDirectory + "/tournaments.dat");
        copyFile(backupDirectory + "/exchange_items.dat", dataDirectory + "/exchange_items.dat");
        copyFile(backupDirectory + "/exchange_requests.dat", dataDirectory + "/exchange_requests.dat");
    }

    // 清理资源
    public void cleanup() {
        // 清理临时文件或资源
    }
}