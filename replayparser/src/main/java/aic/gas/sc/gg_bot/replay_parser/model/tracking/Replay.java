package aic.gas.sc.gg_bot.replay_parser.model.tracking;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

/**
 * Replay class to mark status of particular replay
 */
public class Replay implements Serializable {

  @Getter
  private final String file;

  @Getter
  private final Optional<File> replayFile;

  @Getter
  @Setter
  private boolean parsedMoreTimes = false;

  Replay(String file, boolean parsedMoreTimes) {
    this.file = file;
    this.replayFile = getFile();
    this.parsedMoreTimes = parsedMoreTimes;
  }

  public Replay(File replayFile) {
    this.file = replayFile.getPath();
    this.replayFile = getFile();
  }

  /**
   * Return replay file if it exists and is replay
   */
  public Optional<File> getFile() {
    File replayFile = new File(file);
    if (replayFile.exists() && replayFile.isFile() && Files.getFileExtension(replayFile.getPath())
        .equals("rep")) {
      return Optional.of(replayFile);
    }
    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Replay replay = (Replay) o;

    return file.equals(replay.file);
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }

  public static class ReplaySerializer implements Serializer<Replay>, Serializable {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull Replay replay) throws IOException {
      out.writeUTF(replay.file);
      out.writeBoolean(replay.parsedMoreTimes);
    }

    @Override
    public Replay deserialize(@NotNull DataInput2 in, int i) throws IOException {
      return new Replay(in.readUTF(), in.readBoolean());
    }
  }
}
