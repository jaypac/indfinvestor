Select msd.scheme_name, msd.category, msd.sub_category, stat.*
from mf_rolling_return_stats stat
         inner join public.mf_scheme_details msd on msd.id = stat.scheme_id;


Select idx.name, stat.*
from INDEX_ROLLING_RETURN_STATS stat
         inner join public.index_details idx on idx.id = stat.index_id;