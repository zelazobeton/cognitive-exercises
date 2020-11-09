import {Injectable} from '@angular/core';
import {Observable, Subject, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {UserDto} from '../../model/user-dto';
import {JwtHelperService} from '@auth0/angular-jwt';
import {isDefined} from '@angular/compiler/src/util';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {AuthForm, ChangePasswordForm, RegisterForm} from '../../model/input-forms';
import {catchError, tap} from 'rxjs/operators';
import {HeaderType} from '../enum/header-type.enum';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {Router} from '@angular/router';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  readonly host = environment.apiUrl;
  private token: string;
  public loggedInUser: Subject<UserDto>;
  private jwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient, private notificationService: NotificationService, private router: Router) {
    this.loggedInUser = new Subject<UserDto>();
    this.loggedInUser.next(this.getUserFromLocalStorage());
  }

  public login(loginForm: AuthForm): Observable<HttpResponse<any> | HttpErrorResponse> {
    //TODO get rid of 'if (response instanceof HttpResponse)'
    return this.http.post<HttpResponse<UserDto> | HttpErrorResponse>(
      `${this.host}/user/login`, loginForm, {observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.sendErrorNotification(NotificationType.ERROR, errorRes.error.message);
          return throwError(errorRes);
        }),
        tap(response => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          this.saveToken(token);
          if (response instanceof HttpResponse) {
            this.addUserToLocalStorage(response.body);
            this.loggedInUser.next(response.body);
          }
        })
      );
  }

  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  public register(registerForm: RegisterForm): Observable<UserDto | HttpErrorResponse> {
    return this.http.post<UserDto | HttpErrorResponse>(
      `${this.host}/user/register`, registerForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: UserDto) => {
          this.notificationService.notify(NotificationType.SUCCESS, `A new account was created for ${response.username}.
          Please check your email for password to log in.`);
        })
      );
  }

  public changePassword(changePasswordForm: ChangePasswordForm) {
    return this.http.post<HttpResponse<string> | HttpErrorResponse>(
      `${this.host}/user/change-password`, changePasswordForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: HttpResponse<string>) => {
          this.notificationService.notify(NotificationType.SUCCESS, `Password successfully changed.`);
        })
      );
  }

  public logout(): void {
    this.removeUserDataFromApp();
    this.router.navigateByUrl('/');
  }

  private removeUserDataFromApp() {
    this.token = null;
    this.loggedInUser.next(null);
    localStorage.removeItem('user');
    localStorage.removeItem(this.tokenKey);
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem(this.tokenKey, token);
  }

  public addUserToLocalStorage(user: UserDto): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalStorage(): UserDto {
    return JSON.parse(localStorage.getItem('user'));
  }

  public getLoggedUsernameFromLocalStorage(): string {
    const user = this.getUserFromLocalStorage();
    return user == null ? null : user.username;
  }

  private loadToken(): void {
    this.token = localStorage.getItem(this.tokenKey);
  }

  public getToken(): string {
    this.loadToken();
    return this.token;
  }

  public isUserLoggedIn(): boolean {
    this.loadToken();
    if (!this.isTokenValid()) {
      this.removeUserDataFromApp();
      return false;
    }
    if (this.jwtHelperService.isTokenExpired(this.token)) {
      return false;
    }
    return true;
  }

  private isTokenValid(): boolean {
    if (isDefined(this.token) && this.token !== '') {
      const tokenSubject = this.jwtHelperService.decodeToken(this.token).sub;
      return this.isTokenSubjectValid(tokenSubject);
    }
    return false;
  }

  private isTokenSubjectValid(tokenSubject: string): boolean {
    return isDefined(tokenSubject) && tokenSubject !== '';
  }
}
