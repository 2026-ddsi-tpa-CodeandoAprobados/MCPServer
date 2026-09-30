package ar.edu.utn.edu.utn.dds.k3003.tools;

public class ToolResponse<T> {
    private boolean ok;
    private String message;
    private T data;

    public ToolResponse() {}

    public ToolResponse(boolean ok, String message, T data) {
        this.ok = ok;
        this.message = message;
        this.data = data;
    }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    @Override
    public String toString() {
        return "ToolResponse{" +
                "ok=" + ok +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}