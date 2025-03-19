import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { UserStorageService } from '../services/user-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    // Проверяем, авторизован ли пользователь (ADMIN или USER)
    if (UserStorageService.isAdminLoggedIn() || UserStorageService.isUserLoggedIn()) {
      return true;
    }
    // Если нет – перенаправляем на страницу логина
    this.router.navigate(['/login']);
    return false;
  }
}
