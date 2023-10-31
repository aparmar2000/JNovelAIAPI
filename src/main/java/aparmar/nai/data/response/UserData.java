package main.java.aparmar.nai.data.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class UserData {
	private UserPriority priority;
	private UserSubscription subscription;
	private UserKeystore keystore;
	private String settings;
	private UserInfo information;
}
