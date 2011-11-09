package daid.sliceAndDaid.util;

public interface LoggingInterface
{
	void updateStatus(String status);
	void message(String message);
	void warning(String warning);
	void error(String error);
	void setProgress(int value, int max);
}
