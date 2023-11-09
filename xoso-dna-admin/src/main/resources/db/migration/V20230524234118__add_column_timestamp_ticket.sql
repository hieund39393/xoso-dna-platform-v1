ALTER TABLE public.lottery_ticket ADD created_date timestamp;
ALTER TABLE public.lottery_ticket ADD modified_date timestamp;
ALTER TABLE public.lottery_ticket ADD created_by character varying(100);
ALTER TABLE public.lottery_ticket ADD modified_by character varying(100);
