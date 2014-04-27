package se.michaelthelin.spotify.methods;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.SettableFuture;
import net.sf.json.JSONObject;
import se.michaelthelin.spotify.JsonUtil;
import se.michaelthelin.spotify.SpotifyProtos.Album;
import se.michaelthelin.spotify.SpotifyProtos.AlbumType;
import se.michaelthelin.spotify.exceptions.BadFieldException;
import se.michaelthelin.spotify.exceptions.NotFoundException;
import se.michaelthelin.spotify.exceptions.UnexpectedResponseException;

import java.io.IOException;

public class AlbumRequest extends AbstractRequest {

  public AlbumRequest(Builder builder) {
    super(builder);
  }

  public SettableFuture<Album> getAlbumAsync() {
    SettableFuture<Album> albumFuture = SettableFuture.create();

    try {
      String jsonString = getJson();
      JSONObject jsonObject = JSONObject.fromObject(jsonString);
      if (errorInJson(jsonObject)) {
        Exception exception = getExceptionFromJson(jsonObject);
        albumFuture.setException(exception);
      } else {
        albumFuture.set(JsonUtil.createAlbum(jsonString));
      }
    } catch (IOException e) {
      albumFuture.setException(e);
    } catch (UnexpectedResponseException e) {
      albumFuture.setException(e);
    }

    return albumFuture;
  }

  public Album getAlbum() throws IOException, UnexpectedResponseException, NotFoundException, BadFieldException {
    String jsonString = getJson();
    JSONObject jsonObject = JSONObject.fromObject(jsonString);
    throwIfErrorsInResponse(jsonObject);
    return JsonUtil.createAlbum(getJson());
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractRequest.Builder<Builder> {

    /**
     * The album with the given id.
     *
     * @param id The id for the album.
     * @return AlbumRequest
     */
    public Builder id(String id) {
      assert (id != null);
      return path(String.format("/v1/albums/%s", id));
    }

    public Builder types(AlbumType... types) {
      assert (types != null);
      assert (types.length > 0);
      String albumsParameter = Joiner.on(",").join(types).toString();
      return parameter("album_type", albumsParameter);
    }

    public Builder limit(int limit) {
      assert (limit > 0);
      return parameter("limit", String.valueOf(limit));
    }

    public Builder offset(int offset) {
      assert (offset >= 0);
      return parameter("offset", String.valueOf(offset));
    }

    public AlbumRequest build() {
      return new AlbumRequest(this);
    }

  }

}