#ifndef __EQUIPMENT_H__
#define __EQUIPMENT_H__

int equipment_init(void);
int equipment_get(int type_id, EQUIPMENT_S *tmp_equipment);
int equipments_get(void *tmp_equipments);
int equipment_refresh(void);

#endif
