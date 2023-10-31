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
public class UserInfo {
	private boolean emailVerified, emailVerificationLetterSent;
	private boolean trialActivated;
	private int trialActionsLeft;
	private long accountCreatedAt;
}
