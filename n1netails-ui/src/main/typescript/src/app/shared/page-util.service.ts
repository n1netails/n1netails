import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PageRequest } from '../model/interface/page.interface';

@Injectable({
  providedIn: 'root'
})
export class PageUtilService {

  constructor() { }

  getPageRequestParams(pageRequest: PageRequest): HttpParams {
    let params = new HttpParams()
      .set('pageNumber', pageRequest.pageNumber)
      .set('pageSize', pageRequest.pageSize)
      .set('sortDirection', pageRequest.sortDirection)
      .set('sortBy', pageRequest.sortBy);

    if (pageRequest.searchTerm) {
      params = params.set('searchTerm', pageRequest.searchTerm);
    }
    return params;
  }
}
